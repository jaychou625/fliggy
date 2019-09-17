package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.mapper.Fliggy_hotel_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DOTW新增酒店信息service类
 */
@Service
public class Fliggy_hotel_infoService {

    @Resource
    private Fliggy_hotel_infoMapper fliggy_hotel_infoMapper;

    /**
     * 根据酒店对象新增酒店入库
     * @param fliggy_hotel_info
     * @return
     */
    public  boolean add(Fliggy_hotel_info fliggy_hotel_info){
        return fliggy_hotel_infoMapper.add(fliggy_hotel_info);
    }

    /**
     * 根据酒店id查询条目数
     * @param outer_id
     * @return
     */
    public int findHotelCountById(String outer_id){
        return fliggy_hotel_infoMapper.findHotelCountById(outer_id);
    }

    /**
     * 查询所有待添加酒店信息
     * @return
     */
    public List<Fliggy_hotel_info> searchAllHotel(){
        return fliggy_hotel_infoMapper.searchAllHotel();
    }

    /**
     * 更新酒店所在城市信息
     * @param fliggy_hotel_info
     * @return
     */
    public boolean updateCity(Fliggy_hotel_info fliggy_hotel_info){
        return fliggy_hotel_infoMapper.updateCity(fliggy_hotel_info);
    }

    /**
     * 更新酒店状态信息，0：未添加入飞猪。1：已添加入飞猪
     * @param fliggy_hotel_info
     * @return
     */
    public boolean updateStateAndDate(Fliggy_hotel_info fliggy_hotel_info){
        return fliggy_hotel_infoMapper.updateStateAndDate(fliggy_hotel_info);
    }
}
