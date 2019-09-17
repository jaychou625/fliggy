package com.webbeds.fliggy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 飞猪城市对照表mapper
 */
@Mapper
@Transactional
public interface Fliggy_oversea_cityMapper {

    /**
     * 根据城市名和国家名获取城市ID
     * @param cityName
     * @return
     */
    public String findCityIdByName(String cityName,String countryName);

    /**
     * 根据国家名获取国家ID
     * @param countryName
     * @return
     */
    public String findCountryIdByName(String countryName);
}
