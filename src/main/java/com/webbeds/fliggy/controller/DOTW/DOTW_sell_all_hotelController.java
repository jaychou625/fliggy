package com.webbeds.fliggy.controller.DOTW;

import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.service.DOTW.DOTW.DOTW_sell_all_hotelService;
import com.webbeds.fliggy.task.DotwHotelTask;
import com.webbeds.fliggy.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 酒店信息控制器
 */
@RestController
@Slf4j
public class DOTW_sell_all_hotelController {

    @Autowired
    DOTW_sell_all_hotelService dotw_sell_all_hotelService;

    @Autowired
    DotwHotelTask dotwHotelTask;

    @Autowired
    Common common;

    //根据酒店状态做出相应操作并返回excel
    @RequestMapping(value = "/notEmptyOprate",params = "state",method = RequestMethod.GET)
    public List<JSONObject> notEmptyOprate(String state){
        Long start = new Date().getTime();
//        List<String> list = dotw_sell_all_hotelService.findAllHotelByState(state);
        List<String> list = null;
        try {
            list = common.excel2String("C:\\工作簿2.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JSONObject> listJSON = dotwHotelTask.oprate(list,state);

        common.JSONToExcel(listJSON,state);

        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");

        return listJSON;
    }

}
