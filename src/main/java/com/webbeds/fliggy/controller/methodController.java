package com.webbeds.fliggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import com.webbeds.fliggy.utils.searchUtils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提交方法控制器
 */
@RestController
@Slf4j
public class methodController {

    @Autowired
    Common common;

    @Autowired
    DOTW_hotel_infoService dotw_hotel_infoService;

    @Autowired
    Hotel_info_controller hotel_info_controller;

    @Autowired
    SearchUtils searchUtils;

    @Autowired
    Fliggy_interface_util fliggy_interface_util;

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    /**
     * 提交dotw酒店信息链接方法
     *
     * @param file
     * @return
     */
    @RequestMapping("/fileInputMethod")
    public ModelAndView fileInputMethod(@RequestParam("file") MultipartFile file) {
        boolean flag = false;
        ModelAndView modelAndView = new ModelAndView();
        //读取客户端上传的文件
        long startTime = System.currentTimeMillis();
        System.out.println("fileName：" + file.getOriginalFilename());
        String path = "D:/webbeds/temp/" + file.getOriginalFilename();

        File newFile = new File(path);
        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(newFile);
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("采用file.Transto的运行时间：" + String.valueOf(endTime - startTime) + "ms");

        try {
            List<DOTW_hotel_info> list = common.excel2Bean(path);
            for (DOTW_hotel_info dotw_hotel_info : list) {
                if (dotw_hotel_infoService.searchByHid(dotw_hotel_info.getHotelCode()) == 0) {
                    if(dotw_hotel_info.getReservationTelephone() == null){
                        dotw_hotel_info.setReservationTelephone("00");
                    }
                    dotw_hotel_infoService.add(dotw_hotel_info);
                }
            }
            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        if (flag = true) {
            modelAndView.setViewName("/index");
        } else {
            modelAndView.setViewName("/insertError");
        }
        return modelAndView;
    }


    /**
     * 下载report
     *
     * @param fileName
     * @return
     */
    @RequestMapping("/downloadExcelReport")
    public String downloadExcelReport(String fileName, HttpServletRequest request, HttpServletResponse response) {

        String res = "";
        try {
            common.download(request, response);
            res = "success";
        } catch (IOException e) {
            res = "error";
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 点击调用dotw接口插入数据入本地库
     *
     * @param
     * @return
     */
    @RequestMapping("/addHotelInfo2LocalAnd2StepTwo")
    public ModelAndView addHotelInfo2LocalAnd2StepTwo() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.addHotelInLocalDatabase();
        modelAndView.setViewName("/addHotelInfo2LocalAnd2StepTwoSuccess");
        return modelAndView;
    }

    /**
     * 添加信息入飞猪本地库
     *
     * @param
     * @return
     */
    @RequestMapping("/updateHotelIntoFliggy")
    public ModelAndView updateHotelIntoFliggy() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.updateCity();
        hotel_info_controller.addHotelAndRoom();
        modelAndView.setViewName("/updateHotelIntoFliggySuccess");
        return modelAndView;
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     *
     * @param
     * @return
     */
    @RequestMapping("/searchFliggyHotelInfo")
    public ModelAndView searchFliggyHotelInfo() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.searchHotel();
        modelAndView.setViewName("/searchFliggyHotelInfoSuccess");
        return modelAndView;
    }

    /**
     * 酒店查价专用方法
     *
     * @param file
     * @return
     */
    @RequestMapping("/searchPriceByHid")
    public ModelAndView searchPriceByHid(@RequestParam("file") MultipartFile file, @RequestParam("account") String account, @RequestParam("password") String password, @RequestParam("accountId") String accountId, @RequestParam("version") String version, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) {
        boolean flag = false;
        ModelAndView modelAndView = new ModelAndView();
        flag = searchUtils.searchPriceByHidOprate(file, account, password, accountId, version, fromDate, toDate);
        if (flag) {
            modelAndView.addObject("account", account);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            modelAndView.addObject("date", date);
            modelAndView.addObject("fromDate", fromDate);
            modelAndView.addObject("toDate", toDate);
            modelAndView.setViewName("downLoadPriceInfo");
        } else {
            modelAndView.setViewName("/insertError");
        }
        return modelAndView;
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddHotel")
    public String tempAddHotel() {
        String hid = "1479648,2270745,2123465,1925255,911425,2283255,1602318,584445,2244655";
        String[] hids = hid.split(",");
        for(String id : hids){
            Fliggy_hotel_info fliggy_hotel_info = fliggy_hotel_infoService.findHotelById(id);
            if(fliggy_hotel_info != null){
                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
//                System.out.println(result);
            }else{
//                System.out.println("系统里没有这个酒店：" + id);
            }
        }
        return "";
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddRooms")
    public String tempAddRooms() {
//        String hid = "2532775";
        List<Fliggy_roomType_info> fliggy_roomType_infos = fliggy_roomTpye_infoService.searchRoomByState("9");
        for(Fliggy_roomType_info fliggy_roomType_info : fliggy_roomType_infos){
            if(fliggy_roomType_info != null){
                String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                if (resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1) {
                    System.out.println("房型添加成功!房型id:" + fliggy_roomType_info.getOuter_id());
                    fliggy_roomType_info.setInsertDate(new Date());
                    fliggy_roomType_info.setState("1");
                    fliggy_roomType_info.setError_msg("");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                } else {
//                            System.out.println("房型添加失败");
                    String error_msg = resRoom;
                    fliggy_roomType_info.setError_msg(error_msg);
                    fliggy_roomType_info.setState("2");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                }
            }
        }
//        String[] hids = hid.split(",");
//        for(String id : hids){
//            Fliggy_hotel_info fliggy_hotel_info = fliggy_hotel_infoService.findHotelById(id);
//            if(fliggy_hotel_info != null){
//                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
////                System.out.println(result);
//            }else{
////                System.out.println("系统里没有这个酒店：" + id);
//            }
//        }
        return "";
    }

    /**
     * 推送所有酒店
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddAllHotel")
    public String tempAddAllHotel() {
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        for(Fliggy_hotel_info fliggy_hotel_info : list){
            if(fliggy_hotel_info != null){
                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
                System.out.println(result);
//                System.out.println(result);
            }else{
//                System.out.println("系统里没有这个酒店：" + id);
            }
        }
        return "";
    }

    /**
     * 删除飞猪房型 慎用
     *
     * @param
     * @return
     */
    @RequestMapping("/delRoom_dotwebk")
    public String delRoom_FR() {
        //获取需要删除的房型信息
        //获取需要删除房型的信息
        List<Fliggy_roomType_info> list = fliggy_roomTpye_infoService.searchDelRoom();
        for (Fliggy_roomType_info fliggy_roomType_info_fr : list) {
            String msg = fliggy_interface_util.xRoomDel(fliggy_roomType_info_fr.getOuter_id());
            System.out.println(msg);
            fliggy_roomType_info_fr.setState("-1");
            fliggy_roomType_info_fr.setInsertDate(null);
            fliggy_roomType_info_fr.setError_msg(null);
            fliggy_roomType_info_fr.setError_msg(msg);
            fliggy_roomType_info_fr.setOuter_id(fliggy_roomType_info_fr.getOuter_id());
            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info_fr);
        }
        return "";
    }
}
