package com.webbeds.fliggy.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_interface.City_coordinates;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.entity.Fliggy_roomtype_sub_sort;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_oversea_cityService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_sub_sortService;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HotelInfoAddTask {

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

//    @Scheduled(fixedRate = 10000000)
    public void getHotelInfo(DOTW_hotel_info dotw_hotel_info){

        getHotelInfoInDotwByHotelId(dotw_hotel_info);
    }

    /**
     * 根据城市id查詢DOTW系統返回所有的酒店及房型信息
     * @param
     * @return
     */
    private void getHotelInfoInDotwByHotelId(DOTW_hotel_info dotw_hotel_info){
        //API开发接口URL
        String path = "http://us.DOTWconnect.com/gatewayV3.dotw";

        String xml = "";

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<customer>\n" +
                "    <username>AlitripXML</username>\n" +
                "    <password>CD84D683CC5612C69EFE115C80D0B7DC</password>\n" +
                "    <id>1494305</id>\n" +
                "    <source>1</source>\n" +
                "    <product>hotel</product>\n" +
                "     <request command=\"searchhotels\">  \n" +
                "        <bookingDetails>  \n" +
                "            <fromDate>2019-05-28</fromDate>  \n" +
                "            <toDate>2019-05-29</toDate>  \n" +
                "            <currency>520</currency>  \n" +
                "            <rooms no=\"1\">  \n" +
                "                <room runno=\"0\">  \n" +
                "                    <adultsCode>1</adultsCode>  \n" +
                "                    <children no=\"0\"></children>  \n" +
                "                    <rateBasis>-1</rateBasis>  \n" +
                "                </room>  \n" +
                "            </rooms>  \n" +
                "        </bookingDetails>  \n" +
                "        <return>  \n" +
                "            <getRooms>true</getRooms>  \n" +
                "            <filters xmlns:a=\"http://us.dotwconnect.com/xsd/atomicCondition\" xmlns:c=\"http://us.dotwconnect.com/xsd/complexCondition\">\n" +
//                "              <city>" + cityId + "</city>\n" +
                "              <noPrice>true</noPrice>\n" +
                "              <c:condition>\n" +
                "                <a:condition>\n" +
                "                <fieldName>hotelId</fieldName>\n" +
                "                  <fieldTest>in</fieldTest>\n" +
                "                  <fieldValues>\n" +
                "                    <fieldValue>" + dotw_hotel_info.getHotelCode() + "</fieldValue>\n" +
//                "                    <fieldValue>634155</fieldValue>\n" +
//                "            \t\t<fieldValue>323765</fieldValue>\n" +
                "                  </fieldValues>\n" +
                "                </a:condition>\n" +
                "              </c:condition>\n" +
                "            </filters>  \n" +
                "            <fields>\n" +
                "            \t<field>hotelName</field>  \n" +
                "                <field>chain</field>  \n" +
                "                <field>cityName</field>  \n" +
                "                <field>cityCode</field>  \n" +
                "                <field>stateName</field>  \n" +
                "                <field>stateCode</field>  \n" +
                "                <field>countryName</field>  \n" +
                "                <field>countryCode</field>  \n" +
                "                <field>regionName</field>  \n" +
                "                <field>regionCode</field> \n" +
                "                <field>preferred</field>  \n" +
                "                <field>builtYear</field>  \n" +
                "                <field>renovationYear</field>  \n" +
                "                <field>floors</field>  \n" +
                "                <field>noOfRooms</field>  \n" +
                "                <field>preferred</field>  \n" +
                "                <field>luxury</field>  \n" +
                "                <field>fullAddress</field>  \n" +
                "                <field>description1</field>  \n" +
                "                <field>description2</field>  \n" +
                "                <field>address</field>  \n" +
                "                <field>zipCode</field>  \n" +
                "                <field>location</field>  \n" +
                "                <field>locationId</field>  \n" +
                "                <field>location1</field>  \n" +
                "                <field>location2</field>  \n" +
                "                <field>location3</field>  \n" +
                "                <field>attraction</field>  \n" +
                "                <field>amenitie</field>  \n" +
                "                <field>leisure</field>  \n" +
                "                <field>business</field>  \n" +
                "                <field>transportation</field>  \n" +
                "                <field>hotelPhone</field>  \n" +
                "                <field>hotelCheckIn</field>  \n" +
                "                <field>hotelCheckOut</field>  \n" +
                "                <field>minAge</field>  \n" +
                "                <field>rating</field>  \n" +
                "                <field>images</field>  \n" +
                "                <field>fireSafety</field>  \n" +
                "                <field>hotelPreference</field>  \n" +
                "                <field>direct</field>  \n" +
                "                <field>geoPoint</field>  \n" +
                "                <field>leftToSell</field>  \n" +
                "                <field>chain</field>  \n" +
                "                <field>lastUpdated</field>  \n" +
                "                <field>priority</field>  \n" +
                "                <roomField>name</roomField>  \n" +
                "                <roomField>roomInfo</roomField>  \n" +
                "                <roomField>roomAmenities</roomField>  \n" +
                "                <roomField>twin</roomField>  \n" +
                "            </fields>  \n" +
                "        </return>  \n" +
                "    </request>\n" +
                "</customer>");
        xml = sb.toString();
        try {
            URL url = new URL(path);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "application/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = xml;
            out.write(new String(xmlInfo.getBytes("UTF-8")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            String line = "";
            String xmlString = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                xmlString += line;
            }
            XMLSerializer xmlSerializer = new XMLSerializer();
            String resutStr = xmlSerializer.read(xmlString).toString();
            com.alibaba.fastjson.JSONObject result = JSON.parseObject(resutStr);
            hotelJSONHandler(result,dotw_hotel_info.getHotelCode(),dotw_hotel_info);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * DOTW返回酒店数据处理函数
     * @param result
     */
    private void hotelJSONHandler(com.alibaba.fastjson.JSONObject result, String hid,DOTW_hotel_info dotw_hotel_info){
        if(result.getJSONObject("hotels").get("hotel") == null){
            System.out.println("酒店信息为空,酒店id:" + hid);
            return;
        }
        String classType = result.getJSONObject("hotels").get("hotel").getClass().getName();
        String roomClassType = result.getJSONObject("hotels").getJSONObject("hotel").getJSONObject("rooms").getJSONObject("room").get("roomType").getClass().getName();
        //添加酒店信息
        if(classType.indexOf("Array") != -1){
            for(int i = 0; i < result.getJSONObject("hotels").getJSONArray("hotel").size(); i++){
                //创建实体类
                int count = fliggyhotelinfoService.findHotelCountById(hid);
                if(count == 0){
                    Fliggy_hotel_info fliggy_hotel_info = getInfoByJSONObject(result.getJSONObject("hotels").getJSONArray("hotel").getJSONObject(i),dotw_hotel_info);
                    fliggyhotelinfoService.add(fliggy_hotel_info);
                }
            }
        }else{
            int count = fliggyhotelinfoService.findHotelCountById(hid);
            if(count == 0){
                Fliggy_hotel_info fliggy_hotel_info = getInfoByJSONObject(result.getJSONObject("hotels").getJSONObject("hotel"),dotw_hotel_info);
                fliggyhotelinfoService.add(fliggy_hotel_info);
            }
        }

        if(roomClassType.indexOf("Array") != -1){
            //添加房型信息
            JSONArray rooms = result.getJSONObject("hotels").getJSONObject("hotel").getJSONObject("rooms").getJSONObject("room").getJSONArray("roomType");
            for(int roomsIndex = 0; roomsIndex < rooms.size(); roomsIndex++){
                Fliggy_roomType_info fliggy_roomType_info = getRoomInfoByJSONObject(rooms.getJSONObject(roomsIndex),hid);
                if(fliggy_roomTpye_infoService.searchRoomByRid(fliggy_roomType_info) == 0){
                    fliggy_roomTpye_infoService.add(fliggy_roomType_info);
                }
            }
        }else{
            //添加房型信息
            JSONObject room = result.getJSONObject("hotels").getJSONObject("hotel").getJSONObject("rooms").getJSONObject("room").getJSONObject("roomType");
            Fliggy_roomType_info fliggy_roomType_info = getRoomInfoByJSONObject(room,hid);
            if(fliggy_roomTpye_infoService.searchRoomByRid(fliggy_roomType_info) == 0){
                fliggy_roomTpye_infoService.add(fliggy_roomType_info);
            }
        }
    }

    /**
     * 根据json获取酒店信息实体类
     * @param jsonObject
     * @return
     */
    private Fliggy_hotel_info getInfoByJSONObject(com.alibaba.fastjson.JSONObject jsonObject,DOTW_hotel_info dotw_hotel_info){
        //创建实体类
        Fliggy_hotel_info fliggy_hotel_info = new Fliggy_hotel_info();
//        String hId = jsonObject.getString("@hotelid");
        fliggy_hotel_info.setOuter_id(dotw_hotel_info.getHotelCode());
//        String hName = jsonObject.getString("hotelName");
        fliggy_hotel_info.setHotel_name(dotw_hotel_info.getHotelName());
//        String used_hName = jsonObject.getString("hotelName");
        fliggy_hotel_info.setUsed_name(dotw_hotel_info.getHotelName());
        Integer domestic = dotw_hotel_info.getCountry().equals("CHINA") ? 0 : 1;
        fliggy_hotel_info.setDomestic(domestic);
        String[] countryArr = dotw_hotel_info.getCountry().split(" ");
        String countryName = "";
        for(String str : countryArr){
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
        if(countryId != null){
            city_coordinates.setCountryId(Long.valueOf(countryId));
        }else{
            if(dotw_hotel_info.getCountry().equals("TAIWAN") || dotw_hotel_info.getCountry().equals("MACAU") || dotw_hotel_info.getCountry().equals("HONG KONG") || dotw_hotel_info.getCountry().equals("CHINA")){

            }else{
                System.out.println(countryName);
            }
            city_coordinates.setCountryId(0L);
        }
        city_coordinates.setLatitude(dotw_hotel_info.getLatitude());
        city_coordinates.setLongitude(dotw_hotel_info.getLongitude());
        city_coordinates.setOuterId(dotw_hotel_info.getHotelCode());
        String batch_id = fliggy_interface_util.city_coordinates_batch_upload(city_coordinates);
        fliggy_hotel_info.setBatch_id(batch_id);
        if(dotw_hotel_info.getCountry().equals("TAIWAN") || dotw_hotel_info.getCountry().equals("MACAU") || dotw_hotel_info.getCountry().equals("HONG KONG") || dotw_hotel_info.getCountry().equals("CHINA")){
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
        String decorate_time  = "";
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
//                String imagePathJSON = "[";
//                //判断image字段的类型，又坑，只有一张image时会转为object，多张时转换为JSONArray
//                classType = jsonObject.getJSONObject("images").getJSONObject("hotelImages").get("image").getClass().getName();
//                if(classType.indexOf("Array") != -1){
//                    int imgSize = jsonObject.getJSONObject("images").getJSONObject("hotelImages").getJSONArray("image").size();
//                    imgSize = imgSize > 10 ? 10 : imgSize;
//                    imagesPath = new String[imgSize];
//                    for(int imgSizeIndex = 0; imgSizeIndex < imgSize; imgSizeIndex++){
//                        imagesPath[imgSizeIndex] = jsonObject.getJSONObject("images").getJSONObject("hotelImages").getJSONArray("image").getJSONObject(imgSizeIndex).getString("url");
//                        if(imgSizeIndex == 0){
//                            imagePathJSON += "{\"url\":\" + imagesPath[imgSizeIndex] +\",\"ismain\":\"true\",\"type\" :\"外观\",\"attribute\":\"普通图\"},";
//                        }else if(imgSizeIndex < imgSize - 1){
//                            imagePathJSON += "{\"url\":\" + imagesPath[imgSizeIndex] +\",\"ismain\":\"false\",\"type\" :\"外观\",\"attribute\":\"普通图\"},";
//                        }else{
//                            imagePathJSON += "{\"url\":\" + imagesPath[imgSizeIndex] +\",\"ismain\":\"false\",\"type\" :\"外观\",\"attribute\":\"普通图\"}";
//                        }
//                    }
//                }else{
//                    imagesPath = new String[1];
//                    imagesPath[0] = jsonObject.getJSONObject("images").getJSONObject("hotelImages").getJSONObject("image").getString("url");
//                    imagePathJSON += "{\"url\":\"" + imagesPath[0] + "\",\"ismain\":\"true\",\"type\" :\"外观\",\"attribute\":\"普通图\"}";
//                }
//                imagePathJSON += "]";

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
        String supplier = "";
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
     * @param jsonObject
     * @return
     */
    private Fliggy_roomType_info getRoomInfoByJSONObject(com.alibaba.fastjson.JSONObject jsonObject,String hid){
        Fliggy_roomType_info fliggy_roomType_info = new Fliggy_roomType_info();
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
        if(nameTotal.length() > 27){
            String[] rootRoomType = new String[]{"Room","Dorm","Bungalow","Dormitory","Suite","Villa","Twin","Apartment","Queen","Double","Bed","House"};
            boolean flag = true;
            List<Integer> indexList = new ArrayList<>();
            Map<Integer,String> indexListReverse = new HashMap<Integer,String>();
            for(int i = 0; i < rootRoomType.length;i++){
                int subIndex = nameTotal.lastIndexOf(rootRoomType[i]) > -1 ? nameTotal.lastIndexOf(rootRoomType[i]) : nameTotal.lastIndexOf(rootRoomType[i].toUpperCase());
                if(subIndex == -1){
                    subIndex = nameTotal.lastIndexOf(rootRoomType[i].toLowerCase());
                }
                if(subIndex > -1){
                    int subLength = subIndex + rootRoomType[i].length();
                    if(subLength <= 27){
                        indexList.add(subLength);
                        flag = false;
                    }else{
                        indexListReverse.put(subLength,rootRoomType[i]);
                    }
                }
            }

            //判断是否找到关键字，flag=true未找到关键字
            if(flag){
                //判断如果出现多次关键字， 比较出最大值
                Integer temp1 = 0;
                Integer temp2 = 0;
                for (Map.Entry<Integer, String> entry : indexListReverse.entrySet()) {
                    if(temp1 < entry.getKey()){
                        temp1 = entry.getKey();
                        temp2 = entry.getValue().length();
                    }
                }
                String nameBefore = nameTotal.substring(temp1- temp2,temp1);
                fliggy_roomType_info.setName_before(nameBefore);
                fliggy_roomType_info.setName_after(nameTotal.substring(0,temp1- temp2));
            }else{
                //判断如果出现多次关键字， 比较出最大值
                if(indexList.size() > 1){
                   indexList.sort(Comparator.reverseOrder());
                }
                String nameBefore = nameTotal.substring(0,indexList.get(0));
                fliggy_roomType_info.setName_before(nameBefore);
                fliggy_roomType_info.setName_after(nameTotal.substring(indexList.get(0)));
            }
            fliggy_roomType_info.setName_final(getFinalName(fliggy_roomType_info,false));
        }else{
            fliggy_roomType_info.setName_before(nameTotal);
            fliggy_roomType_info.setName_final(getFinalName(fliggy_roomType_info,true));
        }

        fliggy_roomType_info.setState("0");
        return  fliggy_roomType_info;
    }

    /**
     * 根据相关信息获取finalName,小于28字符flag为true，大于28flag为false
     * @param fliggy_roomType_info
     * @return
     */
    private String getFinalName(Fliggy_roomType_info fliggy_roomType_info,boolean flag){
        String finalName = "";
        Integer codeNo = fliggy_roomTpye_infoService.searchDuplicate(fliggy_roomType_info) + 1;
        if(flag){

            finalName = fliggy_roomType_info.getName_before() + " 0" + codeNo;
        }else{
            //添加房型细节内容入库
            Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort = getFliggy_roomtype_sub_sort(fliggy_roomType_info);
            if(fliggy_roomtype_sub_sort.getSub_id() != null){
                fliggy_roomTpye_sub_sortService.add(fliggy_roomtype_sub_sort);
            }
            Integer subCodeNo = Integer.parseInt(fliggy_roomTpye_sub_sortService.searchSub_idBySub_strAndHid(fliggy_roomtype_sub_sort));
            finalName = fliggy_roomType_info.getName_before() + " " + Integer.toHexString(subCodeNo) + Integer.toHexString(codeNo);
        }
        return finalName;
    }

    /**
     * 获取房型细节信息实体类
     * @param fliggy_roomType_info
     * @return
     */
    private Fliggy_roomtype_sub_sort getFliggy_roomtype_sub_sort(Fliggy_roomType_info fliggy_roomType_info){
        Fliggy_roomtype_sub_sort fliggy_roomtype_sub_sort = new Fliggy_roomtype_sub_sort();
        fliggy_roomtype_sub_sort.setOuter_hid(fliggy_roomType_info.getOut_hid());
        fliggy_roomtype_sub_sort.setOuter_rid(fliggy_roomType_info.getOuter_id());
        fliggy_roomtype_sub_sort.setSub_str(fliggy_roomType_info.getName_after());
        Integer temp = fliggy_roomTpye_sub_sortService.searchDuplicateByHidAndName(fliggy_roomtype_sub_sort);
        //根据方法查询获取房型信息具体编码
        if(temp == 0){
            Integer subId = fliggy_roomTpye_sub_sortService.searchDuplicateByHid(fliggy_roomtype_sub_sort);
            if(subId == null){
                fliggy_roomtype_sub_sort.setSub_id("1");
            }else{
                subId = subId + 1;
                fliggy_roomtype_sub_sort.setSub_id(subId.toString());
            }
        }
        return fliggy_roomtype_sub_sort;
    }
}
