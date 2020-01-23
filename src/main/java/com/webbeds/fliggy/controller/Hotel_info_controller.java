package com.webbeds.fliggy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_interface.City_coordinates;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_oversea_cityService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.task.DotwHotelTask;
import com.webbeds.fliggy.task.HotelInfoAddTask;
import com.webbeds.fliggy.thread.SearchPriceThread;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.DOTW_interface_util;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import com.webbeds.fliggy.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 酒店信息控制器
 */
@RestController
@Slf4j
public class Hotel_info_controller {

    @Autowired
    HotelInfoAddTask hotelInfoAddTask;

    @Autowired
    DOTW_hotel_infoService dotw_hotel_infoService;

    @Autowired
    Fliggy_interface_util fliggy_interface_util;

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    @Autowired
    Common common;

    @Autowired
    DotwHotelTask dotwHotelTask;

    @Autowired
    Fliggy_oversea_cityService fliggy_oversea_cityService;

    @Autowired
    DOTW_interface_util dotw_interface_util;


    /**
     * 读取需要添加的酒店id，根据id获取信息添加入数据库
     */
    @RequestMapping("/addHotelInLocalDatabase")
    public void addHotelInLocalDatabase() {
        //新接口，调用酒店id，批处理提升效率
        Long start = new Date().getTime();
        List<String> list = dotw_hotel_infoService.findAllId();
        System.out.println("共有" + list.size() + "条数据");
        List<JSONObject> listJSON = dotwHotelTask.oprateByThread(list, "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        common.JSONToExcel(listJSON, "alitrip账户有信息的酒店" + date);
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     * 调用飞猪接口更新酒店所在城市id
     */
    @RequestMapping("/updateCity")
    public void updateCity() {
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByCity();
        System.out.println("共计：" + list.size() + "条信息");
        List<List<Fliggy_hotel_info>> listThread = common.splitList(list, 1000);
        CountDownLatch latch = new CountDownLatch(listThread.size());
        //多线程更新城市cityid
        for (List<Fliggy_hotel_info> listTemp : listThread) {
            SearchPriceThread searchPriceThread = new SearchPriceThread(listTemp, common, "updateCity", latch);
            Thread t = new Thread(searchPriceThread);
            t.start();
        }
//        common.updateCityId(list);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行结束");

    }

    /**
     * 询价接口
     * 询价逻辑：根据调用当前日期的后30天 60天 90天进行询价，30天有价设置have_price:1,60天:2 90天：3无价：-1
     */
    @RequestMapping("/searchPrice")
    public void searchPrice() {
        log.info("执行第一次询价开始");
        Long start = new Date().getTime();
        //获取需要询价的飞猪酒店对象
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        System.out.println("共计：" + list.size() + "条信息");
        List<List<Fliggy_hotel_info>> listThread = common.splitList(list, 1000);
        CountDownLatch latch = new CountDownLatch(listThread.size());
        //多线程询价
        for (List<Fliggy_hotel_info> listTemp : listThread) {
            SearchPriceThread searchPriceThread = new SearchPriceThread(listTemp, common, "first", latch);
            Thread t = new Thread(searchPriceThread);
            t.start();
        }
        //询价
//        common.searchHotelPrice(list);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行询价结束");
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     * 询价接口- second
     * 询价逻辑：根据调用当前日期的后30天每一天进行查询价格，查询到价格后更新状态
     */
    @RequestMapping("/searchPriceAgain")
    public void searchPriceAgain() {
        log.info("执行再次询价接口开始");
        Long start = new Date().getTime();
        //获取需要询价的飞猪酒店对象,-1为之前未查到价格的酒店
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByHavePrice("-1");
        System.out.println("共计：" + list.size() + "条信息");
        List<List<Fliggy_hotel_info>> listThread = common.splitList(list, 100);
        CountDownLatch latch = new CountDownLatch(listThread.size());
        //多线程询价
        for (List<Fliggy_hotel_info> listTemp : listThread) {
            SearchPriceThread searchPriceThread = new SearchPriceThread(listTemp, common, "second", latch);
            Thread t = new Thread(searchPriceThread);
            t.start();
        }
        //询价
//        common.searchHotelPriceAgain(list);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行询价结束");
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     * 调用飞猪接口添加酒店信息入飞猪本地库
     */
    @RequestMapping("/addHotelAndRoom")
    public void addHotelAndRoom() {
        log.info("执行添加酒店开始");
        Long start = new Date().getTime();
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByState("0");
        System.out.println("共计：" + list.size() + "条信息");
        List<List<Fliggy_hotel_info>> listThread = common.splitList(list, list.size() / 2);
        CountDownLatch latch = new CountDownLatch(listThread.size());
        //多线程添加酒店与房型入飞猪
        for (List<Fliggy_hotel_info> listTemp : listThread) {
            SearchPriceThread searchPriceThread = new SearchPriceThread(listTemp, common, "addHotel2Fliggy", latch);
            Thread t = new Thread(searchPriceThread);
            t.start();
        }
//        common.add2Fliggy(list);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行添加酒店结束");
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     * 查询酒店信息并输出
     */
    @RequestMapping("/searchHotel")
    public void searchHotel() {
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByState("1");
        System.out.println("共计：" + list.size() + "条信息");
        common.searchAndReport(list);
        System.out.println("接口执行完毕");
    }

//    @RequestMapping("/delAllHotel")
//    public void delAllHotel(){
//        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
//        common.delHotelAndRoom(list);
//        System.out.println("接口执行完毕");
//    }

    /**
     * 更新阿里酒店所在城市的批次号（有些在添加酒店时没加上的给加上批次号）
     */
    @RequestMapping("/updateBatchId")
    public void updateBatchId() {
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchHotelByBatchId();
        common.updateCityBatch(list);
    }


    /**
     * 测试多线程效率
     */
    @RequestMapping("/testMethod")
    public void testMethod() {
        JSONObject json = fliggy_interface_util.xHotelSearch("28744");
        System.out.println(json.toString());
    }


    /**
     * 获取ip测试
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getIp", method = RequestMethod.GET)
    public String getIp(HttpServletRequest request) {
        System.out.println(IpUtils.getIpAddr(request));
        return IpUtils.getIpAddr(request);
    }

    @RequestMapping("/delRoom")
    public void delRoom() {
        List<Fliggy_roomType_info> list = fliggy_roomTpye_infoService.searchRoomByState("0");
        common.delRoom(list);
        System.out.println("接口执行完毕");
    }


    /**
     * 调用飞猪接口添加酒店信息入飞猪本地库（根据酒店id推送酒店信息和房型信息）
     */
    @RequestMapping("/addHotelAndRoomsByHid")
    public void addHotelAndRoomsByHid() {
        log.info("执行添加酒店开始");
        Long start = new Date().getTime();
//        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByState("0");
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        System.out.println("共计：" + list.size() + "条信息");
        List<List<Fliggy_hotel_info>> listThread = common.splitList(list, list.size() / 2);
        CountDownLatch latch = new CountDownLatch(listThread.size());
        //多线程添加酒店与房型入飞猪
        for (List<Fliggy_hotel_info> listTemp : listThread) {
            SearchPriceThread searchPriceThread = new SearchPriceThread(listTemp, common, "addRoomOnly", latch);
            Thread t = new Thread(searchPriceThread);
            t.start();
        }
//        common.add2Fliggy(list);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行添加酒店结束");
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }


    //测试接口,通过酒店id查询酒店信息
    @RequestMapping("/searchFliggyHotels")
    public void searchFliggyHotels() {
        String hid = "1479648,2270745,2123465,1925255,911425,2283255,1602318,584445,2244655";
        String[] hids = hid.split(",");
        List<JSONObject> listHotel = new ArrayList<>();
        System.out.println("长度：" + hids.length);
        for(String id : hids){
            JSONObject jsonObject = fliggy_interface_util.xHotelSearch(id).getJSONObject("xhotel_get_response").getJSONObject("xhotel");
            System.out.println(jsonObject.toJSONString());
            listHotel.add(jsonObject);
        }
        common.JSONToExcel2007(listHotel,"fliggy_hotel_info");
    }

}
