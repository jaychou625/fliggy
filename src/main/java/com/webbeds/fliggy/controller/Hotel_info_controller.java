package com.webbeds.fliggy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.task.HotelInfoAddTask;
import com.webbeds.fliggy.utils.Common;
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
    @RequestMapping("/addHotelAndRoom")
    public void addHotelAndRoom(){
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
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
                    fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
                    //添加酒店对应房型信息入库
                    List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
                    for(Fliggy_roomType_info fliggy_roomType_info : roomList){
                        String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                        if(resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1){
                            System.out.println("房型添加成功");
                            fliggy_roomType_info.setInsertDate(new Date());
                            fliggy_roomType_info.setState("1");
                            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                        }else{
                            System.out.println("房型添加失败");
                            String error_msg = resRoom;
                            fliggy_roomType_info.setError_msg(error_msg);
                            fliggy_roomType_info.setState("2");
                            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                        }
                    }

                }else{
//                    System.out.println("酒店添加失败");
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
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
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

}
