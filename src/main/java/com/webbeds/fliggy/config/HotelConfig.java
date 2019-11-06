package com.webbeds.fliggy.config;

import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotels_infoService;
import com.webbeds.fliggy.task.DotwHotelTask;
import com.webbeds.fliggy.task.FR_hotel.FR_HotelInfoAddTask;
import com.webbeds.fliggy.task.HotelInfoAddTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HotelConfig {
    @Bean
    public HotelInfoAddTask hotelInfoAddTask() {
        return new HotelInfoAddTask();
    }

    @Bean
    public FR_HotelInfoAddTask fr_hotels_infoService() {
        return new FR_HotelInfoAddTask();
    }

    @Bean
    public DotwHotelTask dotwHotelTask() {
        return new DotwHotelTask();
    }
}
