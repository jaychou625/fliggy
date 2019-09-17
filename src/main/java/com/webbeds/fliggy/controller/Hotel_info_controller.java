package com.webbeds.fliggy.controller;

import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.mapper.Fliggy_hotel_infoMapper;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_id_infoService;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_oversea_cityService;
import com.webbeds.fliggy.task.HotelInfoAddTask;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 读取需要添加的酒店id，根据id获取信息添加入数据库
     */
    @RequestMapping("/addHotelInLocalDatabase")
    public void addHotelInLocalDatabase(){
        //查询本地数据库获取需要添加的酒店信息
        List<DOTW_hotel_info> list = dotw_hotel_infoService.findAll();
        //读取DOTW数据入本地库
        for(int i = 0; i < list.size(); i++){
            hotelInfoAddTask.getHotelInfo(list.get(i));
        }
    }

    /**
     * 调用飞猪接口更新酒店所在城市id
     */
    @RequestMapping("/updateCity")
    public void updateCity(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
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
     *调用飞猪接口添加酒店信息入飞猪本地库
     */
    @RequestMapping("/testAddHotel")
    public void testAddHotel(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        for (Fliggy_hotel_info fliggy_hotel_info : list){
            if(fliggy_hotel_info.getOuter_id().equals("100502")){
                String res = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
                if(res != null && res.indexOf("xhotel_add_response") != -1){
                    System.out.println("酒店添加成功");
                }else{
                    System.out.println("酒店添加失败");
                }
            }
        }
    }

}
