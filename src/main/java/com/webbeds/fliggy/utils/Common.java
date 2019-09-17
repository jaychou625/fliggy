package com.webbeds.fliggy.utils;

/**
 * 通用工具类
 */
public class Common {
    //首字母转大写
    public String firstCharacterUpper(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    //特殊，根据特殊城市名获取城市对应的id
    public Integer getCityIdByCityName(String cityName){
        Integer cityId = 0;
        switch (cityName){
            case "HUALIEN" :
                cityId = 712600;
                break;
            case "KAOHSIUNG" :
                cityId = 710200;
                break;
            case "NEW TAIPEI CITY" :
                cityId = 711100;
                break;
            case "TAICHUNG" :
                cityId = 710400;
                break;
            case "TAIPEI" :
                cityId = 710100;
                break;
            case "HONG KONG" :
                cityId = 810100;
                break;
            case "MACAU - MO" :
                cityId = 820100;
                break;
            case "SHANGHAI" :
                cityId = 310100;
                break;
            default:
                cityId = 0;
                break;
        }
        return cityId;
    }

}
