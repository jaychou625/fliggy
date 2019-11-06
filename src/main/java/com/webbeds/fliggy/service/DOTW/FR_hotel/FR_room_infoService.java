package com.webbeds.fliggy.service.DOTW.FR_hotel;


import com.webbeds.fliggy.entity.FR_hotels.FR_room_info;
import com.webbeds.fliggy.mapper.FR_hotel.FR_room_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DOTW新增酒店信息service类
 */
@Service
public class FR_room_infoService {

    @Resource
    private FR_room_infoMapper fr_room_infoMapper;

    /**
     * 根据酒店对象新增酒店入库
     *
     * @param fr_room_info
     * @return
     */
    public boolean add(FR_room_info fr_room_info) {
        return fr_room_infoMapper.add(fr_room_info);
    }

}
