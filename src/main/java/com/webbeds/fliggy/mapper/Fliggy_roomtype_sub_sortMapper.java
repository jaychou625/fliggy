package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.Fliggy_roomtype_sub_sort;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional
public interface Fliggy_roomtype_sub_sortMapper {

    /**
     * 添加房型细节信息
     *
     * @param fliggy_roomtype_sub_sort
     * @return
     */
    public boolean add(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort);

    //根据酒店id，查询同酒店同房型细节条目数
    public Integer searchDuplicateByHid(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort);

    //根据酒店id,酒店细节房型名，查询同酒店同房型细节条目数
    public Integer searchDuplicateByHidAndName(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort);

    //根据细节房型名查询细节id
    public String searchSub_idBySub_strAndHid(Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort);
}
