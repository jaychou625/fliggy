package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.DOTW_hotel_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface DOTW_hotel_infoMapper {
    public List<DOTW_hotel_info> findAll();
}
