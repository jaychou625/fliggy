package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.DOTW_hotel_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * dotw酒店信息类
 */
@Mapper
@Transactional
public interface DOTW_hotel_infoMapper {
    //查询所有dotw酒店
    public List<DOTW_hotel_info> findAll();

    //查询所有dotw酒店的id
    public List<String> findAllId();

    //添加酒店信息
    public boolean add(DOTW_hotel_info dotw_hotel_info);

    //根据酒店id查询条目数
    public Integer searchByHid(String hid);

    //根据酒店id查询酒店
    public DOTW_hotel_info searchHotelByHid(String hid);

    //更新酒店的插入信息
    public boolean updateIsUpdate(String hid, String state);

    //根据酒店id查询全局酒店
    public String searchFullHotelByHid(String hid);
}
