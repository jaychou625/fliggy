package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.entity.Fliggy_roomtype_sub_sort;
import com.webbeds.fliggy.mapper.Fliggy_roomType_infoMapper;
import com.webbeds.fliggy.mapper.Fliggy_roomtype_sub_sortMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DOTW新增细节房型信息service类
 */
@Service
public class Fliggy_roomTpye_sub_sortService {

    @Resource
    private Fliggy_roomtype_sub_sortMapper fliggy_roomtype_sub_sortMapper;

    /**
     * 根据房型细节信息对象新增房型细节信息入库
     *
     * @param fliggy_roomtype_sub_sort
     * @return
     */
    public boolean add(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort) {
        return fliggy_roomtype_sub_sortMapper.add(fliggy_roomtype_sub_sort);
    }

    /**
     * 根据酒店id，查询同酒店同房型细节条目数
     *
     * @param fliggy_roomtype_sub_sort
     * @return
     */
    public Integer searchDuplicateByHid(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort) {
        return fliggy_roomtype_sub_sortMapper.searchDuplicateByHid(fliggy_roomtype_sub_sort);
    }

    /**
     * 根据酒店id,酒店细节房型名，查询同酒店同房型细节条目数
     *
     * @param fliggy_roomtype_sub_sort
     * @return
     */
    public Integer searchDuplicateByHidAndName(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort) {
        return fliggy_roomtype_sub_sortMapper.searchDuplicateByHidAndName(fliggy_roomtype_sub_sort);
    }

    public String searchSub_idBySub_strAndHid(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort) {
        return fliggy_roomtype_sub_sortMapper.searchSub_idBySub_strAndHid(fliggy_roomtype_sub_sort);
    }

}
