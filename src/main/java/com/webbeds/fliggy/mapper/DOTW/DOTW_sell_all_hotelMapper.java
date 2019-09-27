package com.webbeds.fliggy.mapper.DOTW;

import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface DOTW_sell_all_hotelMapper {
    //根据状态返回酒店id
    public List<String> findAllHotelByState(String state);

    public List<String> findAllHotelByReview(String review);
}
