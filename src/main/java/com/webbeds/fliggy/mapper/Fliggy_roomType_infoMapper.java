package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional
public interface Fliggy_roomType_infoMapper {

    //添加酒店房型信息
    public boolean add(Fliggy_roomType_info fliggy_roomType_info);

    //根据酒店id，房型名称查询房型条目数
    public Integer searchDuplicate(Fliggy_roomType_info fliggy_roomType_info);
}
