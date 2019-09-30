package com.webbeds.fliggy.controller;

import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.utils.Common;
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

    /**
     * 提交dotw酒店信息链接方法
     * @param file
     * @return
     */
    @RequestMapping("/fileInputMethod")
    public ModelAndView fileInputMethod(@RequestParam("file") MultipartFile file) {
        boolean flag = false;
        ModelAndView modelAndView = new ModelAndView();
        //读取客户端上传的文件
        long  startTime=System.currentTimeMillis();
        System.out.println("fileName："+file.getOriginalFilename());
        String path="D:/webbeds/temp/"+file.getOriginalFilename();

        File newFile=new File(path);
        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(newFile);
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        long  endTime=System.currentTimeMillis();
        System.out.println("采用file.Transto的运行时间："+String.valueOf(endTime-startTime)+"ms");

        try {
            List<DOTW_hotel_info> list = common.excel2Bean(path);
            for(DOTW_hotel_info dotw_hotel_info : list){
                if(dotw_hotel_infoService.searchByHid(dotw_hotel_info.getHotelCode()) == 0){
                    dotw_hotel_infoService.add(dotw_hotel_info);
                }
            }
            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        if(flag = true){
            modelAndView.setViewName("/index");
        }else{
            modelAndView.setViewName("/insertError");
        }
        return modelAndView;
    }


    /**
     * 下载report
     * @param fileName
     * @return
     */
    @RequestMapping("/downloadExcelReport")
    public String downloadExcelReport(String fileName, HttpServletRequest request, HttpServletResponse response){

        String res = "";
        try {
            common.download(request,response);
            res = "success";
        } catch (IOException e) {
            res = "error";
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 点击调用dotw接口插入数据入本地库
     * @param
     * @return
     */
    @RequestMapping("/addHotelInfo2LocalAnd2StepTwo")
    public ModelAndView addHotelInfo2LocalAnd2StepTwo(){
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.addHotelInLocalDatabase();
        modelAndView.setViewName("/addHotelInfo2LocalAnd2StepTwoSuccess");
        return modelAndView;
    }

    /**
     * 添加信息入飞猪本地库
     * @param
     * @return
     */
    @RequestMapping("/updateHotelIntoFliggy")
    public ModelAndView updateHotelIntoFliggy(){
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.updateCity();
        //todo:增加dotw询价步骤
        hotel_info_controller.searchPrice();
        hotel_info_controller.searchPriceAgain();
        hotel_info_controller.addHotelAndRoom();
        modelAndView.setViewName("/updateHotelIntoFliggySuccess");
        return modelAndView;
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     * @param
     * @return
     */
    @RequestMapping("/searchFliggyHotelInfo")
    public ModelAndView searchFliggyHotelInfo(){
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.searchHotel();
        modelAndView.setViewName("/searchFliggyHotelInfoSuccess");
        return modelAndView;
    }
}
