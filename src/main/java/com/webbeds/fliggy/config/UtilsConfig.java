package com.webbeds.fliggy.config;

import com.webbeds.fliggy.task.HotelInfoAddTask;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
    /**
     * 通用工具方法
     * @return
     */
    @Bean
    public Common common() {
        return new Common();
    }

    @Bean
    public Fliggy_interface_util fliggy_interface_util(){
        return new Fliggy_interface_util();
    }
}
