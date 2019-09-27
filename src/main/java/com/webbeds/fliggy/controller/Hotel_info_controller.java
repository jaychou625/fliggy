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
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.DOTW_interface_util;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void addHotelInLocalDatabase(){
//        //查询本地数据库获取需要添加的酒店信息
//        List<DOTW_hotel_info> list = dotw_hotel_infoService.findAll();
//        //读取DOTW数据入本地库
//        for(int i = 0; i < list.size(); i++){
//            hotelInfoAddTask.getHotelInfo(list.get(i));
//        }
        //新接口，调用酒店id，批处理提升效率
        Long start = new Date().getTime();
        List<String> list = dotw_hotel_infoService.findAllId();
        List<JSONObject> listJSON = dotwHotelTask.oprate(list,"");
        common.JSONToExcel(listJSON,"alitrip账户有信息的酒店");

        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     * 调用飞猪接口更新酒店所在城市id
     */
    @RequestMapping("/updateCity")
    public void updateCity(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByCity();
        for (Fliggy_hotel_info fliggy_hotel_info : list){
            if(fliggy_interface_util.coordinates_batch_download(fliggy_hotel_info.getBatch_id()) != null){
                Integer cityId = Integer.valueOf(fliggy_interface_util.coordinates_batch_download(fliggy_hotel_info.getBatch_id()));
                fliggy_hotel_info.setCity(cityId);
                fliggy_hotel_infoService.updateCity(fliggy_hotel_info);
            }else{
                log.warn("没有找到cityid的国家名：" + fliggy_hotel_info.getCountry() + "酒店名：" + fliggy_hotel_info.getHotel_name());
            }
        }
        log.info("执行结束");

    }

    /**
     * 询价接口
     * 询价逻辑：根据调用当前日期的后30天 60天 90天进行询价，30天有价设置have_price:1,60天:2 90天：3无价：-1
     */
    @RequestMapping("/searchPirce")
    public void searchPirce(){
        log.info("执行询价开始");
        Long start = new Date().getTime();
        //获取需要询价的飞猪酒店对象
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        int count = 0;
        for(Fliggy_hotel_info fliggy_hotel_info : list){
            //查询30天是否有价
            fromDate = common.dateFormat(30);
            toDate = common.dateFormat(31);
            jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(),fromDate,toDate);
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            /**
             * count=0未找到有价信息，>0找到有价信息
             */
            if(count > 0){
                fliggy_hotel_info.setHave_price("1");
                fliggy_hotel_info.setHave_price_date(new Date());
                fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                continue;
            }else{
                fromDate = common.dateFormat(60);
                toDate = common.dateFormat(61);
                jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(),fromDate,toDate);
                count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                if(count > 0){
                    fliggy_hotel_info.setHave_price("2");
                    fliggy_hotel_info.setHave_price_date(new Date());
                    fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                    continue;
                }else{
                    fromDate = common.dateFormat(90);
                    toDate = common.dateFormat(91);
                    jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(),fromDate,toDate);
                    count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                    if(count > 0){
                        fliggy_hotel_info.setHave_price("3");
                        fliggy_hotel_info.setHave_price_date(new Date());
                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        continue;
                    }else{
                        fliggy_hotel_info.setHave_price("-1");
                        fliggy_hotel_info.setHave_price_date(new Date());
                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                    }
                }
            }

        }
        log.info("执行询价结束");
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
    }

    /**
     *调用飞猪接口添加酒店信息入飞猪本地库
     */
    @RequestMapping("/addHotelAndRoom")
    public void addHotelAndRoom(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByState("0");
        for (Fliggy_hotel_info fliggy_hotel_info : list){
            /**
             * step 1：添加酒店信息（添加成功后，更新对应本地数据库的state信息为1，insertDate为入库时间）
             * step 2：酒店添加成功后添加对应酒店的房型信息
             */
            if(fliggy_hotel_info.getCity() != 0){
                String res = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
                if(res != null && res.indexOf("xhotel_add_response") != -1){
//                    System.out.println("酒店添加成功");
                    fliggy_hotel_info.setInsertDate(new Date());
                    fliggy_hotel_info.setState("1");
                    fliggy_hotel_info.setError_msg("");
                    fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
                    //添加酒店对应房型信息入库
                    List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
                    for(Fliggy_roomType_info fliggy_roomType_info : roomList){
                        String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                        if(resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1){
//                            System.out.println("房型添加成功");
                            fliggy_roomType_info.setInsertDate(new Date());
                            fliggy_roomType_info.setState("1");
                            fliggy_roomType_info.setError_msg("");
                            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                        }else{
//                            System.out.println("房型添加失败");
                            String error_msg = resRoom;
                            fliggy_roomType_info.setError_msg(error_msg);
                            fliggy_roomType_info.setState("2");
                            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                        }
                    }

                }else{
                    System.out.println("酒店添加失败");
                    String error_msg = res;
                    fliggy_hotel_info.setError_msg(error_msg);
                    fliggy_hotel_info.setState("2");
                     fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
                }
            }
        }
        System.out.println("接口执行完毕");
    }

    /**
     * 查询酒店信息并输出
     */
    @RequestMapping("/searchHotel")
    public void searchHotel(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotelByState("1");
        List<JSONObject> listHotelInfo = new ArrayList<>();
        List<JSONObject> listRoomInfo = new ArrayList<>();
        for(Fliggy_hotel_info fliggy_hotel_info : list){
            JSONObject temp = fliggy_interface_util.xHotelSearch(fliggy_hotel_info.getOuter_id());
            if(temp != null && temp.getJSONObject("xhotel_get_response") != null){
                listHotelInfo.add(temp.getJSONObject("xhotel_get_response").getJSONObject("xhotel"));
                fliggy_hotel_info.setMapping(temp.getJSONObject("xhotel_get_response").getJSONObject("xhotel").getString("data_confirm_str"));
                fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
            }
            //查询房型
            List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
            for(Fliggy_roomType_info fliggy_roomType_info : roomList){
                JSONObject tempRoom = fliggy_interface_util.xRoomSearch(fliggy_roomType_info.getOuter_id());
                if(tempRoom != null && tempRoom.getJSONObject("xhotel_roomtype_get_response") != null){
                    listRoomInfo.add(tempRoom.getJSONObject("xhotel_roomtype_get_response").getJSONObject("xroomtype"));
                    fliggy_roomType_info.setMapping(tempRoom.getJSONObject("xhotel_roomtype_get_response").getJSONObject("xroomtype").getString("data_confirm_str"));
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                }
            }
        }
        //最后输出
        common.JSONToExcel(listHotelInfo,"hotel");
        common.JSONToExcel(listRoomInfo,"roomType");
        System.out.println("接口执行完毕");
    }

    @RequestMapping("/delAllHotel")
    public void delAllHotel(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        /**
         * step 1：删除房型信息
         * step 2：删除酒店信息
         */
        for (Fliggy_hotel_info fliggy_hotel_info : list){
            List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
            for(Fliggy_roomType_info fliggy_roomType_info : roomList){
                fliggy_interface_util.xRoomDel(fliggy_roomType_info.getOuter_id());
                fliggy_roomType_info.setState("0");
                fliggy_roomType_info.setInsertDate(null);
                fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
            }
            fliggy_interface_util.xHotelDel(fliggy_hotel_info.getOuter_id());
            fliggy_hotel_info.setState("0");
            fliggy_hotel_info.setInsertDate(null);
            fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
        }
        System.out.println("接口执行完毕");
    }

    /**
     * 更新阿里酒店所在城市的批次号（有些在添加酒店时没加上的给加上批次号）
     */
    @RequestMapping("/updateBatchId")
    public void updateBatchId(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchHotelByBatchId();
        for(Fliggy_hotel_info fliggy_hotel_info : list){
            //根据飞猪城市协议接口先上传酒店经纬度信息再下载对应的酒店id;
            City_coordinates city_coordinates = new City_coordinates();
            String countryId = fliggy_oversea_cityService.findCountryIdByName(fliggy_hotel_info.getCountry());
            if(countryId != null){
                city_coordinates.setCountryId(Long.valueOf(countryId));
            }else{
                if(fliggy_hotel_info.getCountry().equals("TAIWAN") || fliggy_hotel_info.getCountry().equals("MACAU") || fliggy_hotel_info.getCountry().equals("HONG KONG") || fliggy_hotel_info.getCountry().equals("CHINA")){

                }else{
                    System.out.println(fliggy_hotel_info.getCountry());
                }
                city_coordinates.setCountryId(0L);
            }
            city_coordinates.setLatitude(fliggy_hotel_info.getLatitude());
            city_coordinates.setLongitude(fliggy_hotel_info.getLongitude());
            city_coordinates.setOuterId(fliggy_hotel_info.getOuter_id());
            String batch_id = fliggy_interface_util.city_coordinates_batch_upload(city_coordinates);
            fliggy_hotel_info.setBatch_id(batch_id);
            fliggy_hotel_infoService.updateBatchId(fliggy_hotel_info);
        }
    }

}
