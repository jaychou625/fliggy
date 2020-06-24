package com.webbeds.fliggy.config;

import com.webbeds.fliggy.schedule.AutoUpdateSchedule;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.DOTW_interface_util;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import com.webbeds.fliggy.utils.searchUtils.SearchUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {
    /**
     * 定时任务配置类
     *
     * @return
     */
    @Bean
    public AutoUpdateSchedule autoUpdateSchedule(){
        return new AutoUpdateSchedule();
    }
}
