package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.mapper.Fliggy_roomType_infoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DOTW新增房型信息service类
 */
@Service
public class Fliggy_roomTpye_infoService {

    @Resource
    private Fliggy_roomType_infoMapper fliggy_roomType_infoMapper;

    /**
     * 根据房型对象新增房型入库
     * @param fliggy_roomType_info
     * @return
     */
    public  boolean add(Fliggy_roomType_info fliggy_roomType_info){
        return fliggy_roomType_infoMapper.add(fliggy_roomType_info);
    }

    /**
     * 查询是否有重复房型并返回条目数
     * @param fliggy_roomType_info
     * @return
     */
    public Integer searchDuplicate(Fliggy_roomType_info fliggy_roomType_info){
        return fliggy_roomType_infoMapper.searchDuplicate(fliggy_roomType_info);
    }

}
