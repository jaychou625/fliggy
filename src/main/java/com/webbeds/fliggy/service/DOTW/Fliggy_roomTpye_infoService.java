package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.mapper.Fliggy_roomType_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DOTW新增房型信息service类
 */
@Service
public class Fliggy_roomTpye_infoService {

    @Resource
    private Fliggy_roomType_infoMapper fliggy_roomType_infoMapper;

    /**
     * 根据房型对象新增房型入库
     *
     * @param fliggy_roomType_info
     * @return
     */
    public boolean add(Fliggy_roomType_info fliggy_roomType_info) {
        return fliggy_roomType_infoMapper.add(fliggy_roomType_info);
    }

    /**
     * 查询是否有重复房型并返回条目数
     *
     * @param fliggy_roomType_info
     * @return
     */
    public Integer searchDuplicate(Fliggy_roomType_info fliggy_roomType_info) {
        return fliggy_roomType_infoMapper.searchDuplicate(fliggy_roomType_info);
    }

    /**
     * 根据酒店查询酒店对应房型
     *
     * @param hid
     * @return
     */
    public List<Fliggy_roomType_info> searchRoomByHid(String hid) {
        return fliggy_roomType_infoMapper.searchRoomByHid(hid);
    }

    /**
     * 根据房型id查询房型数量
     *
     * @param fliggy_roomType_info
     * @return
     */
    public Integer searchRoomByRid(Fliggy_roomType_info fliggy_roomType_info) {
        return fliggy_roomType_infoMapper.searchRoomByRid(fliggy_roomType_info);
    }

    /**
     * 更新房型状态信息，0：未添加入飞猪。1：已添加入飞猪
     *
     * @param fliggy_roomType_info
     * @return
     */
    public boolean updateStateAndDate(Fliggy_roomType_info fliggy_roomType_info) {
        return fliggy_roomType_infoMapper.updateStateAndDate(fliggy_roomType_info);
    }

    /**
     * 查询需要删除的房型
     *
     * @param state
     * @return
     */
    public List<Fliggy_roomType_info> searchRoomByState(String state) {
        return fliggy_roomType_infoMapper.searchRoomByState(state);
    }

    //临时，删除房型用
    public List<Fliggy_roomType_info> searchDelRoom(){
        return fliggy_roomType_infoMapper.searchDelRoom();
    }

    //查询所有房型
    public List<Fliggy_roomType_info> searchAllRomms(){
        return fliggy_roomType_infoMapper.searchAllRomms();
    }

}
