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
     *
     * @param fliggy_hotel_info
     * @return
     */
    public boolean add(Fliggy_hotel_info fliggy_hotel_info) {
        return fliggy_hotel_infoMapper.add(fliggy_hotel_info);
    }

    /**
     * 根据酒店id查询条目数
     *
     * @param outer_id
     * @return
     */
    public int findHotelCountById(String outer_id) {
        return fliggy_hotel_infoMapper.findHotelCountById(outer_id);
    }

    /**
     * 根据酒店id查询酒店
     *
     * @param outer_id
     * @return
     */
    public Fliggy_hotel_info findHotelById(String outer_id) {
        return fliggy_hotel_infoMapper.findHotelById(outer_id);
    }

    /**
     * 查询所有待添加酒店信息
     *
     * @return
     */
    public List<Fliggy_hotel_info> searchAllHotel() {
        return fliggy_hotel_infoMapper.searchAllHotel();
    }

    /**
     * 更新酒店所在城市信息
     *
     * @param fliggy_hotel_info
     * @return
     */
    public boolean updateCity(Fliggy_hotel_info fliggy_hotel_info) {
        return fliggy_hotel_infoMapper.updateCity(fliggy_hotel_info);
    }

    /**
     * 更新酒店状态信息，0：未添加入飞猪。1：已添加入飞猪
     *
     * @param fliggy_hotel_info
     * @return
     */
    public boolean updateStateAndDate(Fliggy_hotel_info fliggy_hotel_info) {
        return fliggy_hotel_infoMapper.updateStateAndDate(fliggy_hotel_info);
    }

    /**
     * 查询所有加入飞猪库的酒店信息
     *
     * @return
     */
    public List<Fliggy_hotel_info> searchAllHotelByState(String state) {
        return fliggy_hotel_infoMapper.searchAllHotelByState(state);
    }

    /**
     * //更新酒店信息
     *
     * @param hid
     * @return
     */
    public boolean updateInfo(String hid) {
        return fliggy_hotel_infoMapper.updateInfo(hid);
    }

    /**
     * 获取酒店信息（根据酒店所在城市批次号）
     *
     * @return
     */
    public List<Fliggy_hotel_info> searchHotelByBatchId() {
        return fliggy_hotel_infoMapper.searchHotelByBatchId();
    }

    /**
     * 更新酒店城市处理批次信息
     */
    public boolean updateBatchId(Fliggy_hotel_info fliggy_hotel_info) {
        return fliggy_hotel_infoMapper.updateBatchId(fliggy_hotel_info);
    }

    /**
     * 根据未更新城市id查询酒店信息
     *
     * @return
     */
    public List<Fliggy_hotel_info> searchAllHotelByCity() {
        return fliggy_hotel_infoMapper.searchAllHotelByCity();
    }

    /**
     * 更新酒店是否有价
     *
     * @param fliggy_hotel_info
     * @return
     */
    public boolean updateHavePrice(Fliggy_hotel_info fliggy_hotel_info) {
        return fliggy_hotel_infoMapper.updateHavePrice(fliggy_hotel_info);
    }

    /**
     * 根据是否有价参数查询酒店信息
     *
     * @param havePrice
     * @return
     */
    public List<Fliggy_hotel_info> searchAllHotelByHavePrice(String havePrice) {
        return fliggy_hotel_infoMapper.searchAllHotelByHavePrice(havePrice);
    }

}
