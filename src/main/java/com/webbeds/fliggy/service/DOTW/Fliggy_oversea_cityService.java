package com.webbeds.fliggy.service.DOTW;

import com.webbeds.fliggy.mapper.Fliggy_oversea_cityMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 海外城市service
 */
@Service
public class Fliggy_oversea_cityService {

    @Resource
    public Fliggy_oversea_cityMapper fliggy_oversea_cityMapper;

    public String findCityIdByName(String cityName, String countryName) {
        return fliggy_oversea_cityMapper.findCityIdByName(cityName, countryName);
    }

    public String findCountryIdByName(String countryName) {
        return fliggy_oversea_cityMapper.findCountryIdByName(countryName);
    }

}
