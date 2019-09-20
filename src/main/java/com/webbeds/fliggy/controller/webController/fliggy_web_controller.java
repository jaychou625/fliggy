package com.webbeds.fliggy.controller.webController;

import com.webbeds.fliggy.controller.Hotel_info_controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 飞猪页面路由控制器
 */
@Controller
public class fliggy_web_controller {

    @Autowired
    Hotel_info_controller hotel_info_controller;

    /**
     * 提交数据主页面
     * @param model
     * @return
     */
    @GetMapping("/main")
    public String main(Model model){
//        model.addAttribute("msg","test");
        return "main";
    }

    /**
     * 插入dotw数据成功页面
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String index(Model model){
//        model.addAttribute("msg","test");
        return "index";
    }

    /**
     * 插入数据失败页面
     * @param model
     * @return
     */
    @GetMapping("/insertError")
    public String insertError(Model model){
//        model.addAttribute("msg","test");
        return "insertError";
    }

    /**
     * 点击调用dotw接口插入数据入本地库
     * @param model
     * @return
     */
    @GetMapping("/addHotelInfo2LocalAnd2StepTwoSuccess")
    public String addHotelInfo2LocalAnd2StepTwo(Model model){
//        model.addAttribute("msg","test");
//        hotel_info_controller.addHotelInLocalDatabase();
        return "addHotelInfo2LocalAnd2StepTwoSuccess";
    }

    /**
     * 添加信息入飞猪本地库
     * @param model
     * @return
     */
    @GetMapping("/updateHotelIntoFliggySuccess")
    public String updateHotelIntoFliggy(Model model){
//        hotel_info_controller.updateCity();
//        hotel_info_controller.addHotelAndRoom();
        return "updateHotelIntoFliggySuccess";
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     * @param model
     * @return
     */
    @GetMapping("/searchFliggyHotelInfoSuccess")
    public String searchFliggyHotelInfo(Model model){
//        hotel_info_controller.searchHotel();
        return "searchFliggyHotelInfoSuccess";
    }

}
