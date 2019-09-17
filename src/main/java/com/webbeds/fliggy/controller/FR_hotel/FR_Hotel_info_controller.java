package com.webbeds.fliggy.controller.FR_hotel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotel_image;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotels_info;
import com.webbeds.fliggy.entity.FR_hotels.FR_room_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_id_infoService;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotel_imageService;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotels_infoService;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_room_infoService;
import com.webbeds.fliggy.task.FR_hotel.FR_HotelInfoAddTask;
import com.webbeds.fliggy.task.HotelInfoAddTask;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 酒店信息控制器
 */
@RestController
public class FR_Hotel_info_controller {

    @Autowired
    FR_HotelInfoAddTask fr_hotelInfoAddTask;

    @Autowired
    FR_hotels_infoService fr_hotels_infoService;

    @Autowired
    FR_room_infoService fr_room_infoService;

    @Autowired
    FR_hotel_imageService fr_hotel_imageService;


    /**
     * 读取需要添加的酒店id，根据id获取信息添加入数据库
     */
    @RequestMapping("/addFRHotelInLocalDatabase")
    public void addHotelInLocalDatabase() throws IOException {
        for(int xmlIndex = 39; xmlIndex <=40; xmlIndex++){
            System.out.println("正在执行hotels" + xmlIndex + ".xml");
            long startTime = new Date().getTime();
            File file = new File("D:/WEBBEDS/常用文件/FR全量酒店信息/hotelsInfo/hotels" + xmlIndex + ".xml");
            LineIterator it = FileUtils.lineIterator(file, "UTF-8");
            StringBuilder sb = new StringBuilder();
            try {
                while (it.hasNext()) {
                    sb.append(it.nextLine());
                    // do something with line
                }
            } finally {
                XMLSerializer xmlSerializer = new XMLSerializer();
                String temp = sb.toString();
//            String xmlString = temp.substring(temp.indexOf("<room_types>"),temp.indexOf("</room_types>") + "</room_types>".length());
                String resutStr = "{\"hotels\":";
                resutStr += xmlSerializer.read(temp).toString() + "}";
                com.alibaba.fastjson.JSONObject result = JSON.parseObject(resutStr);
                Integer size = result.getJSONArray("hotels").size();
                for(int i = 0; i < size; i++){
                    //创建hotel实体类
                    FR_hotels_info fr_hotels_info = fr_hotelInfoAddTask.getHotelInfoByJSONObject(result.getJSONArray("hotels").getJSONObject(i));

                    if(result.getJSONArray("hotels").getJSONObject(i).getJSONArray("rooms") != null){
                        //room实体类
                        for(int roomIndex = 0; roomIndex < result.getJSONArray("hotels").getJSONObject(i).getJSONArray("rooms").size(); roomIndex++){
                            //创建room实体类
                            FR_room_info room_info = fr_hotelInfoAddTask.getRoomInfoByJSONObject(result.getJSONArray("hotels").getJSONObject(i).getJSONArray("rooms").getJSONObject(roomIndex),fr_hotels_info.getId());
                            fr_room_infoService.add(room_info);
                        }
                    }

                    if(result.getJSONArray("hotels").getJSONObject(i).getJSONArray("images") != null){
                        //image实体类
                        for(int imageIndex = 0; imageIndex < result.getJSONArray("hotels").getJSONObject(i).getJSONArray("images").size(); imageIndex++){
                            //创建room实体类
                            FR_hotel_image fr_hotel_image = fr_hotelInfoAddTask.getImageInfoByJSONObject(result.getJSONArray("hotels").getJSONObject(i).getJSONArray("images").getJSONObject(imageIndex),fr_hotels_info.getId());
                            fr_hotel_imageService.add(fr_hotel_image);
                        }
                    }

                    fr_hotels_infoService.add(fr_hotels_info);
                }
                LineIterator.closeQuietly(it);
            }

            long endTime = new Date().getTime();
            System.out.println("执行完毕,执行时间:" + (endTime - startTime) / 1000 + "秒");
        }
    }

}
