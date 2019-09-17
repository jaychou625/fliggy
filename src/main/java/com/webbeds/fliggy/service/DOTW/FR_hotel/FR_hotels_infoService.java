package com.webbeds.fliggy.service.DOTW.FR_hotel;

import com.webbeds.fliggy.entity.FR_hotels.FR_hotels_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.mapper.FR_hotel.FR_hotels_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DOTW新增酒店信息service类
 */
@Service
public class FR_hotels_infoService {

    @Resource
    private FR_hotels_infoMapper fr_hotels_infoMapper;

    /**
     * 根据酒店对象新增酒店入库
     * @param fr_hotels_info
     * @return
     */
    public  boolean add(FR_hotels_info fr_hotels_info){
        return fr_hotels_infoMapper.add(fr_hotels_info);
    }

}
