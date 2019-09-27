package com.webbeds.fliggy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotels_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotels_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.utils.Common;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.webbeds.fliggy.mapper")
public class FliggyApplication {


    public static void main(String[] args) {
        SpringApplication.run(FliggyApplication.class, args);
    }

    @Test
    public void contextLoads() {
        Common common = new Common();
        String str = common.subStringSpecial("SANCTUARY BEACH POOL VILLA");
        System.out.println("输出：" + str);
    }


}
