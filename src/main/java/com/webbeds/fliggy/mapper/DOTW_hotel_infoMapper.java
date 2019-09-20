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

    //添加酒店信息
    public boolean add(DOTW_hotel_info dotw_hotel_info);

    //根据酒店id查询条目数
    public Integer searchByHid(String hid);
}
