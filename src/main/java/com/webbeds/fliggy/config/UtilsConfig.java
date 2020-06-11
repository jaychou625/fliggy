package com.webbeds.fliggy.config;

import com.webbeds.fliggy.task.HotelInfoAddTask;
import com.webbeds.fliggy.utils.*;
import com.webbeds.fliggy.utils.searchUtils.SearchUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
    /**
     * 通用工具方法
     *
     * @return
     */
    @Bean
    public Common common() {
        return new Common();
    }

    @Bean
    public Fliggy_interface_util fliggy_interface_util() {
        return new Fliggy_interface_util();
    }

    @Bean
    public DOTW_interface_util dotw_interface_util() {
        return new DOTW_interface_util();
    }

    @Bean
    public SearchUtils searchUtils() {
        return new SearchUtils();
    }

    @Bean
    public SendMailUtil sendMailUtil() {
        return new SendMailUtil();
    }

    @Bean
    public OutLookUtil outLookUtil() {
        return new OutLookUtil();
    }
}
