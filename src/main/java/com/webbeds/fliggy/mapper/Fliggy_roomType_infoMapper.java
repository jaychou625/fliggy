package com.webbeds.fliggy.mapper;

import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface Fliggy_roomType_infoMapper {

    //添加酒店房型信息
    public boolean add(Fliggy_roomType_info fliggy_roomType_info);

    //根据酒店id、房型名称查询房型条目数
    public Integer searchDuplicate(Fliggy_roomType_info fliggy_roomType_info);

    //根据房型id查询房型数量
    public Integer searchRoomByRid(Fliggy_roomType_info fliggy_roomType_info);

    //根据酒店查询酒店对应房型
    public List<Fliggy_roomType_info> searchRoomByHid(String hid);

    //更新房型状态信息，0：未添加入飞猪。1：已添加入飞猪
    boolean updateStateAndDate(Fliggy_roomType_info fliggy_roomType_info);

    //查询需要删除的房型
    public List<Fliggy_roomType_info> searchRoomByState(String state);

    //临时，删除房型用
    List<Fliggy_roomType_info> searchDelRoom();

    //查询所有房型
    public List<Fliggy_roomType_info> searchAllRomms();

    //更新房型信息
    boolean updateRoomInfo(Fliggy_roomType_info fliggy_roomType_info);

    //根据房型名称查询房型信息
    Fliggy_roomType_info searchRoomInfoByRid(Fliggy_roomType_info fliggy_roomType_info);
}
