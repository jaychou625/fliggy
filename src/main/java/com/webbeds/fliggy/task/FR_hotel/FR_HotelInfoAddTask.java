package com.webbeds.fliggy.task.FR_hotel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotel_image;
import com.webbeds.fliggy.entity.FR_hotels.FR_hotels_info;
import com.webbeds.fliggy.entity.FR_hotels.FR_room_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.service.DOTW.FR_hotel.FR_hotels_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_oversea_cityService;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FR_HotelInfoAddTask {

    /**
     * 飞猪酒店信息实体类获取方法
     *
     * @param jsonObject
     * @return
     */
    public FR_hotels_info getHotelInfoByJSONObject(JSONObject jsonObject) {
        FR_hotels_info fr_hotels_info = new FR_hotels_info();
        fr_hotels_info.setId(jsonObject.getString("id"));
        fr_hotels_info.setHotel_name(jsonObject.getString("name"));
        fr_hotels_info.setAccomodation_type(jsonObject.getString("accomodation_type"));
        fr_hotels_info.setAddress(jsonObject.getString("address"));
        fr_hotels_info.setAdult_only(jsonObject.getString("adult_only"));
        fr_hotels_info.setBest_buy(jsonObject.getString("best_buy"));
        fr_hotels_info.setClassification(jsonObject.getString("classification"));
        String desc = jsonObject.getString("description");
        if (desc.length() > 8990) {
            desc = desc.substring(0, 8990);
        }
        fr_hotels_info.setDescription(desc);
        fr_hotels_info.setEmail(jsonObject.getString("email"));
        fr_hotels_info.setFax(jsonObject.getString("fax"));
        String headline = jsonObject.getString("headline");
        if (headline.length() > 4900) {
            headline = headline.substring(0, 4900);
        }
        fr_hotels_info.setHeadline(headline);
        fr_hotels_info.setInfant_restrictions(jsonObject.getString("infant_restrictions"));
        fr_hotels_info.setGeoposition(jsonObject.getString("position"));
        fr_hotels_info.setPhone(jsonObject.getString("phone"));
        fr_hotels_info.setPlace(jsonObject.getString("place"));
        fr_hotels_info.setFeatures(jsonObject.getString("features"));
        fr_hotels_info.setDistances(jsonObject.getString("distances"));
        fr_hotels_info.setReviews(jsonObject.getString("reviews"));
        fr_hotels_info.setThemes(jsonObject.getString("themes"));
        return fr_hotels_info;
    }

    /**
     * room信息实体类
     *
     * @param jsonObject
     * @param hid
     * @return
     */
    public FR_room_info getRoomInfoByJSONObject(JSONObject jsonObject, String hid) {
        FR_room_info fr_room_info = new FR_room_info();
        fr_room_info.setHid(hid);
        fr_room_info.setBest_buy(jsonObject.getString("best_buy`"));
        fr_room_info.setMeal_supplement_restriction(jsonObject.getString("meal_supplement_restriction"));
        fr_room_info.setRoom_id(jsonObject.getString("room_id"));
        fr_room_info.setType_id(jsonObject.getString("@type_id"));
        return fr_room_info;
    }

    public FR_hotel_image getImageInfoByJSONObject(JSONObject jsonObject, String hid) {
        FR_hotel_image fr_hotel_image = new FR_hotel_image();
        fr_hotel_image.setHid(hid);
        fr_hotel_image.setImg_id(jsonObject.getString("@id"));
        fr_hotel_image.setPath(jsonObject.getString("image_variant"));
        return fr_hotel_image;
    }
}
