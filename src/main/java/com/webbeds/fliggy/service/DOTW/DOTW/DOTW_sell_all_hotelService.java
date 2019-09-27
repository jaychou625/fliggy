package com.webbeds.fliggy.service.DOTW.DOTW;

import com.webbeds.fliggy.entity.DOTW_hotel_id_info;
import com.webbeds.fliggy.mapper.DOTW.DOTW_sell_all_hotelMapper;
import com.webbeds.fliggy.mapper.DOTW_hotel_id_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DOTW酒店IDservice类
 */
@Service
public class DOTW_sell_all_hotelService {
    @Resource
    public DOTW_sell_all_hotelMapper dotw_sell_all_hotelMapper;

    /**
     * 根据酒店状态返回所有酒店id
     * @return
     */
    public List<String> findAllHotelByState(String state){
        return dotw_sell_all_hotelMapper.findAllHotelByState(state);
    }

    /**
     * 根据手动mapping后的状态返回酒店id
     * @param review
     * @return
     */
    public List<String> findAllHotelByReview(String review){
        return dotw_sell_all_hotelMapper.findAllHotelByReview(review);
    }
}
