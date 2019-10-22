package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.mapper.DOTW_hotel_id_infoMapper;
import com.webbeds.fliggy.mapper.DOTW_hotel_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DOTW酒店IDservice类
 */
@Service
public class DOTW_hotel_infoService {
    @Resource
    public DOTW_hotel_infoMapper dotw_hotel_infoMapper;

    /**
     * 返回所有酒店id和酒店名信息
     * @return
     */
    public List<DOTW_hotel_info> findAll(){
        return dotw_hotel_infoMapper.findAll();
    }

    /**
     * 查询所有酒店id
     * @return
     */
    public List<String> findAllId(){
        return dotw_hotel_infoMapper.findAllId();
    }

    /**
     * 添加dotw酒店
     * @param dotw_hotel_info
     * @return
     */
    public boolean add(DOTW_hotel_info dotw_hotel_info){
        return dotw_hotel_infoMapper.add(dotw_hotel_info);
    }

    /**
     * 根据酒店id查询条目数
     * @param hid
     * @return
     */
    public Integer searchByHid(String hid){
        return dotw_hotel_infoMapper.searchByHid(hid);
    }

    /**
     * 根据酒店id查询酒店
     * @param hid
     * @return
     */
    public DOTW_hotel_info searchHotelByHid(String hid){
        return dotw_hotel_infoMapper.searchHotelByHid(hid);
    }

    /**
     * 更新酒店的插入信息
     * @param hid,state
     * @return
     */
    public boolean updateIsUpdate(String hid,String state){
        return dotw_hotel_infoMapper.updateIsUpdate(hid,state);
    }

    /**
     * 根据酒店id查询全局酒店
     */
    public String searchFullHotelByHid(String hid){
        return dotw_hotel_infoMapper.searchFullHotelByHid(hid);
    }

}
