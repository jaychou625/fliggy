package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface Fliggy_hotel_infoMapper {

    //添加酒店信息
    boolean add(Fliggy_hotel_info fliggy_hotel_info);

    //根据id查询
    int findHotelCountById(String outer_id);

    //更新酒店城市信息
    boolean updateCity(Fliggy_hotel_info fliggy_hotel_info);

    //获取所有酒店信息
    List<Fliggy_hotel_info> searchAllHotel();

    //更新酒店状态信息，0：未添加入飞猪。1：已添加入飞猪
    boolean updateStateAndDate(Fliggy_hotel_info fliggy_hotel_info);

    //获取所有已添加入飞猪库的酒店信息
    List<Fliggy_hotel_info> searchAllHotelByState();
}
