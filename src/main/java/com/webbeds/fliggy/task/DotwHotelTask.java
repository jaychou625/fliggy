package com.webbeds.fliggy.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_interface.City_coordinates;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.entity.Fliggy_roomtype_sub_sort;
import com.webbeds.fliggy.service.DOTW.*;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.DOTW_interface_util;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import net.sf.json.xml.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class DotwHotelTask {
    @Autowired
    public Fliggy_hotel_infoService fliggyhotelinfoService;

    @Autowired
    public Common common;

    @Autowired
    public Fliggy_interface_util fliggy_interface_util;

    @Autowired
    Fliggy_oversea_cityService fliggy_oversea_cityService;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    @Autowired
    Fliggy_roomTpye_sub_sortService fliggy_roomTpye_sub_sortService;

    @Autowired
    DOTW_hotel_infoService dotw_hotel_infoService;

    @Autowired
    DOTW_interface_util dotw_interface_util;

    /**
     * dotw数据操作方法（暂定）
     *
     * @param list
     */
    public List<JSONObject> oprate(List<String> list, String state) {
        int lenList = list.size();
        System.out.println("共有：" + lenList + "条消息");
        List<JSONObject> listJSON = new ArrayList<>();
        //根据dotw酒店查询接口获得json对象
        JSONObject jsonObject = dotw_interface_util.getHotelInfoInDotwByHotelId(list);
        //根据hotel的类属性进行处理，判断如果不等于空并且count条目等于1则转换为jsonobjectcount>1则转换为jsonarray
        if (jsonObject != null && !jsonObject.getJSONObject("hotels").getString("@count").equals("0")) {
            JSONObject hotelJSON = null;
            if (jsonObject.getJSONObject("hotels").getString("@count").equals("1")) {
                hotelJSON = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                DOTW_hotel_info dotw_hotel_info = dotw_hotel_infoService.searchHotelByHid(hotelJSON.getString("@hotelid"));
                if (dotw_hotel_info != null) {
                    oprateAddHotelAndRoom2Fliggy(hotelJSON, dotw_hotel_info, listJSON);
                } else {

                }
            } else {
                JSONArray hotelArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                for (int arrIndex = 0; arrIndex < hotelArray.size(); arrIndex++) {
                    hotelJSON = hotelArray.getJSONObject(arrIndex);
                    DOTW_hotel_info dotw_hotel_info = dotw_hotel_infoService.searchHotelByHid(hotelJSON.getString("@hotelid"));
                    if (dotw_hotel_info != null) {
                        oprateAddHotelAndRoom2Fliggy(hotelJSON, dotw_hotel_info, listJSON);
                    } else {

                    }
                }
            }
            //处理完毕后调用添加接口将酒店数据转化为对应的飞猪酒店和房型信息并插入数据库
        } else {
            //如果dotw中没有查询到酒店信息并且酒店状态为酒店停售时，进行更改操作。更新飞猪接口为-2关闭酒店
            if (state.equals("酒店停售")) {
                //todo：关闭酒店接口调用
            }
        }

        return listJSON;
    }

    /**
     * dotw数据操作方法（暂定）
     *
     * @param list
     */
    public List<JSONObject> oprateMore(List<String> list, String state) {
        int lenList = list.size();
        //按多少长度切割的系数
        int constListLen = 30;
        System.out.println("共有：" + lenList + "条消息");
        List<JSONObject> listJSON = new ArrayList<>();

        for (int i = 0; i < lenList / constListLen; i++) {
            List<String> listTemp = new ArrayList<>();
            //分批次处理大量酒店id
            for (int j = i * constListLen; j < i * constListLen + constListLen; j++) {
                listTemp.add(list.get(j));
            }
            //如果是最后一次执行，则补齐余数
            if (i == lenList / constListLen - 1) {
                for (int k = i * constListLen + constListLen; k < lenList; k++) {
                    listTemp.add(list.get(k));
                }
            }
            //根据dotw酒店查询接口获得json对象
            JSONObject jsonObject = dotw_interface_util.getHotelInfoInDotwByHotelId(listTemp);
            //根据hotel的类属性进行处理，判断如果不等于空并且count条目等于1则转换为jsonobjectcount>1则转换为jsonarray
            if (jsonObject != null && !jsonObject.getJSONObject("hotels").getString("@count").equals("0")) {
                JSONObject hotelJSON = null;
                if (jsonObject.getJSONObject("hotels").getString("@count").equals("1")) {
                    hotelJSON = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                    DOTW_hotel_info dotw_hotel_info = dotw_hotel_infoService.searchHotelByHid(hotelJSON.getString("@hotelid"));
                    if (dotw_hotel_info != null) {
                        oprateAddHotelAndRoom2Fliggy(hotelJSON, dotw_hotel_info, listJSON);
                    } else {

                    }
                } else {
                    JSONArray hotelArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                    for (int arrIndex = 0; arrIndex < hotelArray.size(); arrIndex++) {
                        hotelJSON = hotelArray.getJSONObject(arrIndex);
                        DOTW_hotel_info dotw_hotel_info = dotw_hotel_infoService.searchHotelByHid(hotelJSON.getString("@hotelid"));
                        if (dotw_hotel_info != null) {
                            oprateAddHotelAndRoom2Fliggy(hotelJSON, dotw_hotel_info, listJSON);
                        } else {

                        }
                    }
                }
                //处理完毕后调用添加接口将酒店数据转化为对应的飞猪酒店和房型信息并插入数据库
            } else {
                //如果dotw中没有查询到酒店信息并且酒店状态为酒店停售时，进行更改操作。更新飞猪接口为-2关闭酒店
                if (state.equals("酒店停售")) {
                    //todo：关闭酒店接口调用
                }
            }
        }

        return listJSON;
    }

    /**
     * 添加酒店和房型入本地飞猪库
     *
     * @param hotelJSON
     * @param dotw_hotel_info
     * @param listJSON
     */
    public void oprateAddHotelAndRoom2Fliggy(JSONObject hotelJSON, DOTW_hotel_info dotw_hotel_info, List<JSONObject> listJSON) {
        int count = fliggyhotelinfoService.findHotelCountById(hotelJSON.getString("@hotelid"));
        if (count == 0) {
            JSONObject roomJSON = null;
            //添加房型
            if (hotelJSON.getJSONObject("rooms").getJSONObject("room").getString("@count").equals("1")) {
                roomJSON = hotelJSON.getJSONObject("rooms").getJSONObject("room").getJSONObject("roomType");
                Fliggy_roomType_info fliggy_roomType_info = getRoomInfoByJSONObject(roomJSON, hotelJSON.getString("@hotelid"));
                if (fliggy_roomTpye_infoService.searchRoomByRid(fliggy_roomType_info) == 0) {
                    fliggy_roomTpye_infoService.add(fliggy_roomType_info);
                }
            } else {
                JSONArray roomArray = hotelJSON.getJSONObject("rooms").getJSONObject("room").getJSONArray("roomType");
                for (int roomIndex = 0; roomIndex < roomArray.size(); roomIndex++) {
                    roomJSON = roomArray.getJSONObject(roomIndex);
                    Fliggy_roomType_info fliggy_roomType_info = getRoomInfoByJSONObject(roomJSON, hotelJSON.getString("@hotelid"));
                    if (fliggy_roomTpye_infoService.searchRoomByRid(fliggy_roomType_info) == 0) {
                        fliggy_roomTpye_infoService.add(fliggy_roomType_info);
                    }
                }
            }

            //添加酒店
            Fliggy_hotel_info fliggy_hotel_info = getInfoByJSONObject(hotelJSON, dotw_hotel_info);
            fliggyhotelinfoService.add(fliggy_hotel_info);
            dotw_hotel_infoService.updateIsUpdate(hotelJSON.getString("@hotelid"), "1");
        } else {
            dotw_hotel_infoService.updateIsUpdate(hotelJSON.getString("@hotelid"), "-1");
        }
        listJSON.add(hotelJSON);
    }

//    /**
//     * 根据hotelid调用dotw接口获取酒店和房型信息
//     * @param listTemp
//     * @return
//     */
//    public JSONObject getHotelInfoInDotwByHotelId(List<String> listTemp){
//        //API开发接口URL
//        String path = "http://us.DOTWconnect.com/gatewayV3.dotw";
//        JSONObject result = null;
//
//        String xml = "";
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
//        sb.append("<customer>\n" +
//                "    <username>AlitripXML</username>\n" +
//                "    <password>CD84D683CC5612C69EFE115C80D0B7DC</password>\n" +
//                "    <id>1494305</id>\n" +
//                "    <source>1</source>\n" +
//                "    <product>hotel</product>\n" +
//                "     <request command=\"searchhotels\">  \n" +
//                "        <bookingDetails>  \n" +
//                "            <fromDate>2019-05-28</fromDate>  \n" +
//                "            <toDate>2019-05-29</toDate>  \n" +
//                "            <currency>520</currency>  \n" +
//                "            <rooms no=\"1\">  \n" +
//                "                <room runno=\"0\">  \n" +
//                "                    <adultsCode>1</adultsCode>  \n" +
//                "                    <children no=\"0\"></children>  \n" +
//                "                    <rateBasis>-1</rateBasis>  \n" +
//                "                </room>  \n" +
//                "            </rooms>  \n" +
//                "        </bookingDetails>  \n" +
//                "        <return>  \n" +
//                "            <getRooms>true</getRooms>  \n" +
//                "            <filters xmlns:a=\"http://us.dotwconnect.com/xsd/atomicCondition\" xmlns:c=\"http://us.dotwconnect.com/xsd/complexCondition\">\n" +
////                "              <city>" + cityId + "</city>\n" +
//                "              <noPrice>true</noPrice>\n" +
//                "              <c:condition>\n" +
//                "                <a:condition>\n" +
//                "                <fieldName>hotelId</fieldName>\n" +
//                "                  <fieldTest>in</fieldTest>\n" +
//                "                  <fieldValues>\n");
//            for(String str : listTemp){
//                sb.append("<fieldValue>" + str + "</fieldValue>\n" );
//            }
//
//            sb.append("                  </fieldValues>\n" +
//                    "                </a:condition>\n" +
//                    "              </c:condition>\n" +
//                    "            </filters>  \n" +
//                    "            <fields>\n" +
//                    "            \t<field>hotelName</field>  \n" +
//                    "                <field>chain</field>  \n" +
//                    "                <field>cityName</field>  \n" +
//                    "                <field>cityCode</field>  \n" +
//                    "                <field>stateName</field>  \n" +
//                    "                <field>stateCode</field>  \n" +
//                    "                <field>countryName</field>  \n" +
//                    "                <field>countryCode</field>  \n" +
//                    "                <field>regionName</field>  \n" +
//                    "                <field>regionCode</field> \n" +
//                    "                <field>preferred</field>  \n" +
//                    "                <field>builtYear</field>  \n" +
//                    "                <field>renovationYear</field>  \n" +
//                    "                <field>floors</field>  \n" +
//                    "                <field>noOfRooms</field>  \n" +
//                    "                <field>preferred</field>  \n" +
//                    "                <field>luxury</field>  \n" +
//                    "                <field>fullAddress</field>  \n" +
//                    "                <field>description1</field>  \n" +
//                    "                <field>description2</field>  \n" +
//                    "                <field>address</field>  \n" +
//                    "                <field>zipCode</field>  \n" +
//                    "                <field>location</field>  \n" +
//                    "                <field>locationId</field>  \n" +
//                    "                <field>location1</field>  \n" +
//                    "                <field>location2</field>  \n" +
//                    "                <field>location3</field>  \n" +
//                    "                <field>attraction</field>  \n" +
//                    "                <field>amenitie</field>  \n" +
//                    "                <field>leisure</field>  \n" +
//                    "                <field>business</field>  \n" +
//                    "                <field>transportation</field>  \n" +
//                    "                <field>hotelPhone</field>  \n" +
//                    "                <field>hotelCheckIn</field>  \n" +
//                    "                <field>hotelCheckOut</field>  \n" +
//                    "                <field>minAge</field>  \n" +
//                    "                <field>rating</field>  \n" +
//                    "                <field>images</field>  \n" +
//                    "                <field>fireSafety</field>  \n" +
//                    "                <field>hotelPreference</field>  \n" +
//                    "                <field>direct</field>  \n" +
//                    "                <field>geoPoint</field>  \n" +
//                    "                <field>leftToSell</field>  \n" +
//                    "                <field>chain</field>  \n" +
//                    "                <field>lastUpdated</field>  \n" +
//                    "                <field>priority</field>  \n" +
//                    "                <roomField>name</roomField>  \n" +
//                    "                <roomField>roomInfo</roomField>  \n" +
//                    "                <roomField>roomAmenities</roomField>  \n" +
//                    "                <roomField>twin</roomField>  \n" +
//                    "            </fields>  \n" +
//                    "        </return>  \n" +
//                    "    </request>\n" +
//                    "</customer>");
//        xml = sb.toString();
//        try {
//            URL url = new URL(path);
//            URLConnection con = url.openConnection();
//            con.setDoOutput(true);
//            con.setRequestProperty("Pragma", "no-cache");
//            con.setRequestProperty("Cache-Control", "no-cache");
//            con.setRequestProperty("Content-Type", "application/xml");
//
//            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
//            String xmlInfo = xml;
//            out.write(new String(xmlInfo.getBytes("UTF-8")));
//            out.flush();
//            out.close();
//            BufferedReader br = new BufferedReader(new InputStreamReader(con
//                    .getInputStream()));
//            String line = "";
//            String xmlString = "";
//            for (line = br.readLine(); line != null; line = br.readLine()) {
//                xmlString += line;
//            }
//            XMLSerializer xmlSerializer = new XMLSerializer();
//            String resutStr = xmlSerializer.read(xmlString).toString();
//            result = JSON.parseObject(resutStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//
//    }


    /**
     * 根据json获取酒店信息实体类
     *
     * @param jsonObject
     * @return
     */
    private Fliggy_hotel_info getInfoByJSONObject(JSONObject jsonObject, DOTW_hotel_info dotw_hotel_info) {
        //创建实体类
        Fliggy_hotel_info fliggy_hotel_info = new Fliggy_hotel_info();
//        String hId = jsonObject.getString("@hotelid");
        fliggy_hotel_info.setOuter_id(dotw_hotel_info.getHotelCode());//finish
//        String hName = jsonObject.getString("hotelName");//finish
        fliggy_hotel_info.setHotel_name(dotw_hotel_info.getHotelName());
//        String used_hName = jsonObject.getString("hotelName");//finish
        fliggy_hotel_info.setUsed_name(dotw_hotel_info.getHotelName());
        Integer domestic = dotw_hotel_info.getCountry().equals("CHINA") ? 0 : 1;
        fliggy_hotel_info.setDomestic(domestic);
        String[] countryArr = dotw_hotel_info.getCountry().split(" ");
        String countryName = "";
        for (String str : countryArr) {
            countryName += common.firstCharacterUpper(str) + " ";
        }
        countryName = countryName.trim();
        fliggy_hotel_info.setCountry(countryName);
        //省份编码根据查询到的字符串匹配对照表数据库
        Integer province = 0;
        fliggy_hotel_info.setProvince(province);
        Integer city = 0;
        //根据飞猪城市协议接口先上传酒店经纬度信息再下载对应的酒店id;
        City_coordinates city_coordinates = new City_coordinates();
        String countryId = fliggy_oversea_cityService.findCountryIdByName(countryName);
        if (countryId != null) {
            city_coordinates.setCountryId(Long.valueOf(countryId));
        } else {
            if (dotw_hotel_info.getCountry().equals("TAIWAN") || dotw_hotel_info.getCountry().equals("MACAU") || dotw_hotel_info.getCountry().equals("HONG KONG") || dotw_hotel_info.getCountry().equals("CHINA")) {

            } else {
                System.out.println(countryName);
            }
            city_coordinates.setCountryId(0L);
        }
        city_coordinates.setLatitude(dotw_hotel_info.getLatitude());
        city_coordinates.setLongitude(dotw_hotel_info.getLongitude());
        city_coordinates.setOuterId(dotw_hotel_info.getHotelCode());
        String batch_id = fliggy_interface_util.city_coordinates_batch_upload(city_coordinates);
        fliggy_hotel_info.setBatch_id(batch_id);
        if (dotw_hotel_info.getCountry().equals("TAIWAN") || dotw_hotel_info.getCountry().equals("MACAU") || dotw_hotel_info.getCountry().equals("HONG KONG") || dotw_hotel_info.getCountry().equals("CHINA")) {
            city = common.getCityIdByCityName(dotw_hotel_info.getCity());
        }
        fliggy_hotel_info.setCity(city);
        Integer district = 0;
        fliggy_hotel_info.setDistrict(district);
        String business = "";
        fliggy_hotel_info.setBusiness(business);
        String address = dotw_hotel_info.getHotelAddress();
        fliggy_hotel_info.setAddress(address);
        String longitude = dotw_hotel_info.getLongitude();
        fliggy_hotel_info.setLongitude(longitude);
        String latitude = dotw_hotel_info.getLatitude();
        fliggy_hotel_info.setLatitude(latitude);
        String position_type = "G";
        fliggy_hotel_info.setPosition_type(position_type);
        String tel = dotw_hotel_info.getReservationTelephone();
        fliggy_hotel_info.setTel(tel);
        String extend = "";
        fliggy_hotel_info.setExtend(extend);
        Integer shid = 0;
        fliggy_hotel_info.setShid(0);
        String vendor = "DOTW";
        fliggy_hotel_info.setVendor(vendor);
        String star = "";
        fliggy_hotel_info.setStar(star);
        String opening_time = "";
        fliggy_hotel_info.setOpening_time(opening_time);
        String decorate_time = "";
        fliggy_hotel_info.setDecorate_time(decorate_time);
        String floors = "";
        fliggy_hotel_info.setFloors(floors);
//        Integer rooms = Integer.parseInt(jsonObject.getString("noOfRooms"));
        Integer rooms = 0;
        fliggy_hotel_info.setRooms(rooms);
        String description = "";
        fliggy_hotel_info.setDescription(description);
        String hotel_policies = "";
        fliggy_hotel_info.setHotel_policies(hotel_policies);
        String hotel_facilities = "";
        fliggy_hotel_info.setHotel_facilities(hotel_facilities);
        String service = "";
        fliggy_hotel_info.setService(service);
        String room_facilities = "";
        fliggy_hotel_info.setRoom_facilities(room_facilities);
        String[] imagesPath = null;
        //酒店图片字段
        String imagePathJSON = "";
        fliggy_hotel_info.setPics(imagePathJSON);

        String postal_code = "";
        fliggy_hotel_info.setPostal_code(postal_code);
        String booking_notice = "";
        fliggy_hotel_info.setBooking_notice(booking_notice);
        String credit_card_types = "";
        fliggy_hotel_info.setCredit_card_types(credit_card_types);
        String orbit_track = "";
        fliggy_hotel_info.setOrbit_track(orbit_track);
        String name_e = "";
        fliggy_hotel_info.setName_e(name_e);
        String supplier = "DOTW";
        fliggy_hotel_info.setSupplier(supplier);
        String settlement_currency = "";
        fliggy_hotel_info.setSettlement_currency(settlement_currency);
        String standard_amuse_facilities = "";
        fliggy_hotel_info.setStandard_amuse_facilities(standard_amuse_facilities);
        String standard_room_facilities = "";
        fliggy_hotel_info.setStandard_room_facilities(standard_room_facilities);
        String standard_hotel_service = "";
        fliggy_hotel_info.setStandard_hotel_service(standard_hotel_service);
        String standard_hotel_facilities = "";
        fliggy_hotel_info.setStandard_hotel_facilities(standard_hotel_facilities);
        String standard_booking_notice = "";
        fliggy_hotel_info.setStandard_booking_notice(standard_booking_notice);
        //设置酒店品牌
        fliggy_hotel_info.setBrand("");
        fliggy_hotel_info.setState("0");
        return fliggy_hotel_info;
    }

    /**
     * 根据json获取房型信息
     *
     * @param jsonObject
     * @return
     */
    private Fliggy_roomType_info getRoomInfoByJSONObject(JSONObject jsonObject, String hid) {
        Fliggy_roomType_info fliggy_roomType_info = new Fliggy_roomType_info();
        fliggy_roomType_info.setVendor("DOTW");
        fliggy_roomType_info.setOut_hid(hid);
        fliggy_roomType_info.setOuter_id(jsonObject.getString("@roomtypecode"));
        fliggy_roomType_info.setName(jsonObject.getString("name"));
        fliggy_roomType_info.setName_e(jsonObject.getString("name"));
        fliggy_roomType_info.setBed_type("大床/双床");
        String nameTotal = jsonObject.getString("name");
        /**
         * 截取关键字，Room、Dorm、Bungalow、Dormitory、Suite、Villa、Twin、APARTMENT
         * 27位同名房型匹配类型，30位同名房型序号（防重复）
         */
        if (nameTotal.length() > 25) {
            String[] rootRoomType = common.getRoomType();
            boolean flag = true;
            List<Integer> indexList = new ArrayList<>();
            Map<Integer, String> indexListReverse = new HashMap<Integer, String>();
            for (int i = 0; i < rootRoomType.length; i++) {
                int subIndex = nameTotal.indexOf(rootRoomType[i]) > -1 ? nameTotal.indexOf(rootRoomType[i]) : nameTotal.indexOf(rootRoomType[i].toUpperCase());
                if (subIndex == -1) {
                    subIndex = nameTotal.indexOf(rootRoomType[i].toLowerCase());
                }
                if (subIndex > -1) {
                    int subLength = subIndex + rootRoomType[i].length();
                    if (subLength <= 25) {
                        indexList.add(subLength);
                        flag = false;
                    }
//                    else{
//                        indexListReverse.put(subLength,rootRoomType[i]);
//                    }
                }
            }

            //判断是否找到关键字，flag=true未找到关键字
            if (flag) {
//                Integer temp1 = 0;
//                Integer temp2 = 0;
//                for (Map.Entry<Integer, String> entry : indexListReverse.entrySet()) {
//                    if(temp1 < entry.getKey()){
//                        temp1 = entry.getKey();
//                        temp2 = entry.getValue().length();
//                    }
//                }
//                String nameBefore = nameTotal.substring(temp1- temp2,temp1);
//                fliggy_roomType_info.setName_before(nameBefore);
//                fliggy_roomType_info.setName_after(nameTotal.substring(0,temp1- temp2));
                String nameBefore = "";
                String nameAfter = "";
                nameBefore = common.subStringSpecial(nameTotal);
                nameAfter = nameTotal;
                fliggy_roomType_info.setName_before(nameBefore);
                fliggy_roomType_info.setName_after(nameAfter);
            } else {
                //判断如果出现多次关键字， 比较出最大值
                if (indexList.size() > 1) {
                    indexList.sort(Comparator.reverseOrder());
                }
                String nameBefore = "";
                String nameAfter = "";
                if (indexList.get(0) == 0) {
                    nameBefore = common.subStringSpecial(nameTotal);
                    nameAfter = nameTotal;
                } else {
                    nameBefore = nameTotal.substring(0, indexList.get(0));
                    nameAfter = nameTotal.substring(indexList.get(0));
                }
                fliggy_roomType_info.setName_before(nameBefore);
                fliggy_roomType_info.setName_after(nameAfter);
            }
            fliggy_roomType_info.setName_final(getFinalName(fliggy_roomType_info, false));
        } else {
            fliggy_roomType_info.setName_before(nameTotal);
            fliggy_roomType_info.setName_final(getFinalName(fliggy_roomType_info, true));
        }

        fliggy_roomType_info.setState("0");
        return fliggy_roomType_info;
    }

    /**
     * 根据相关信息获取finalName,小于28字符flag为true，大于28flag为false
     *
     * @param fliggy_roomType_info
     * @return
     */
    private String getFinalName(Fliggy_roomType_info fliggy_roomType_info, boolean flag) {
        String finalName = "";
        Integer codeNo = fliggy_roomTpye_infoService.searchDuplicate(fliggy_roomType_info) + 1;
        if (flag) {

            finalName = fliggy_roomType_info.getName_before() + " 00" + Integer.toHexString(codeNo);
        } else {
            //添加房型细节内容入库
            Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort = getFliggy_roomtype_sub_sort(fliggy_roomType_info);
            if (fliggy_roomtype_sub_sort.getSub_id() != null) {
                fliggy_roomTpye_sub_sortService.add(fliggy_roomtype_sub_sort);
            }
            Integer subCodeNo = Integer.parseInt(fliggy_roomTpye_sub_sortService.searchSub_idBySub_strAndHid(fliggy_roomtype_sub_sort));
            finalName = fliggy_roomType_info.getName_before() + " " + Integer.toHexString(subCodeNo) + Integer.toHexString(codeNo);
        }
        return finalName;
    }

    /**
     * 获取房型细节信息实体类
     *
     * @param fliggy_roomType_info
     * @return
     */
    private Fliggy_roomtype_sub_sort getFliggy_roomtype_sub_sort(Fliggy_roomType_info fliggy_roomType_info) {
        Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort = new Fliggy_roomtype_sub_sort();
        fliggy_roomtype_sub_sort.setOuter_hid(fliggy_roomType_info.getOut_hid());
        fliggy_roomtype_sub_sort.setOuter_rid(fliggy_roomType_info.getOuter_id());
        fliggy_roomtype_sub_sort.setSub_str(fliggy_roomType_info.getName_after());
        Integer temp = fliggy_roomTpye_sub_sortService.searchDuplicateByHidAndName(fliggy_roomtype_sub_sort);
        //根据方法查询获取房型信息具体编码
        if (temp == 0) {
            Integer subId = fliggy_roomTpye_sub_sortService.searchDuplicateByHid(fliggy_roomtype_sub_sort);
            if (subId == null) {
                fliggy_roomtype_sub_sort.setSub_id("1");
            } else {
                subId = subId + 1;
                fliggy_roomtype_sub_sort.setSub_id(subId.toString());
            }
        }
        return fliggy_roomtype_sub_sort;
    }

}
