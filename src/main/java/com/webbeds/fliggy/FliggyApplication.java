package com.webbeds.fliggy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotels_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotels_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.thread.SearchPriceThread;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.webbeds.fliggy.mapper")
public class FliggyApplication {

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    Common common;

    public static void main(String[] args) {
        SpringApplication.run(FliggyApplication.class, args);
    }

    @Test
    public void contextLoads() {

    }


}
