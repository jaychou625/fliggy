package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import com.webbeds.fliggy.mapper.DOTW_hotel_id_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DOTW酒店IDservice类
 */
@Service
public class DOTW_hotel_id_infoService {
    @Resource
    public DOTW_hotel_id_infoMapper dotw_hotel_id_infoMapper;

    /**
     * 返回所有酒店id和酒店名信息
     *
     * @return
     */
    public List<DOTW_hotel_id_info> findAll() {
        return dotw_hotel_id_infoMapper.findAll();
    }
}
