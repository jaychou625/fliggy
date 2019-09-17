package com.webbeds.fliggy.service.DOTW.FR_hotel;


import com.webbeds.fliggy.entity.FR_hotels.FR_hotel_image;
import com.webbeds.fliggy.entity.FR_hotels.FR_room_info;
import com.webbeds.fliggy.mapper.FR_hotel.FR_hotel_imageMapper;
import com.webbeds.fliggy.mapper.FR_hotel.FR_room_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DOTW新增酒店信息service类
 */
@Service
public class FR_hotel_imageService {

    @Resource
    private FR_hotel_imageMapper fr_hotel_imageMapper;

    /**
     * 根据酒店对象新增酒店入库
     * @param fr_hotel_image
     * @return
     */
    public  boolean add(FR_hotel_image fr_hotel_image){
        return fr_hotel_imageMapper.add(fr_hotel_image);
    }

}
