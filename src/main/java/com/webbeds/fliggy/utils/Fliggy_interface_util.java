package com.webbeds.fliggy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_interface.City_coordinates;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 飞猪接口调用工具类
 */
@Slf4j
public class Fliggy_interface_util {

    //接口调用地址
    private final String url = "http://gw.api.taobao.com/router/rest";
    //appkey
    private final String appkey = "24823522";
    //secret
    private final String secret = "91c56c1988556d2a8c053c730f74e286";
    //sessionKey
    private final String sessionKey = "6101730ee845c089e1d56504a71b87755b4ff61a659da093309205637";

    //沙箱接口调用地址
    private final String urlS = "http://gw.api.tbsandbox.com/router/rest";
    //appkey
    private final String appkeyS = "1024823522";
    //secret
    private final String secretS = "sandbox988556d2a8c053c730f74e286";
    //sessionKey
    private final String sessionKeyS = "61028252845fcd5ace28881ff5ee2301313dcfa6f6456423686530402";


    /**
     * 根据酒店经纬度添加酒店位置信息
     * @param city_coordinates
     * @return
     */
    public String city_coordinates_batch_upload(City_coordinates city_coordinates){
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelCityCoordinatesBatchUploadRequest req = new XhotelCityCoordinatesBatchUploadRequest();
        List<XhotelCityCoordinatesBatchUploadRequest.Coordinate> list2 = new ArrayList<XhotelCityCoordinatesBatchUploadRequest.Coordinate>();
        XhotelCityCoordinatesBatchUploadRequest.Coordinate obj3 = new XhotelCityCoordinatesBatchUploadRequest.Coordinate();
        list2.add(obj3);
        obj3.setCountry(city_coordinates.getCountryId());
        obj3.setLatitude(city_coordinates.getLatitude());
        obj3.setLongitude(city_coordinates.getLongitude());
        obj3.setOuterId(city_coordinates.getOuterId());
        req.setCoordinateList(list2);
        XhotelCityCoordinatesBatchUploadResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        String jsonString = rsp.getBody();
        JSONObject obj = JSON.parseObject(jsonString);
        String str = obj.toString();
        if(str.indexOf("xhotel_city_coordinates_batch_upload_response") != -1){
            str = obj.getJSONObject("xhotel_city_coordinates_batch_upload_response").getString("batch_id");
        }else{
            str = null;
        }
        return str;
    }

    /**
     * 根据批次号获取酒店所在城市id
     * @param batch_id
     * @return
     */
    public String coordinates_batch_download(String batch_id){
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelCityCoordinatesBatchDownloadRequest req = new XhotelCityCoordinatesBatchDownloadRequest();
        Long bid = Long.valueOf(batch_id);
        req.setBatchId(bid);
        XhotelCityCoordinatesBatchDownloadResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
//        log.info(rsp.getBody());
        String cityCode = JSON.parseObject(rsp.getBody()).getJSONObject("xhotel_city_coordinates_batch_download_response").getJSONObject("coordinate_list").getJSONArray("coordinate").getJSONObject(0).getString("city");
        return cityCode;
    }

    /**
     * 新增酒店信息入飞猪标准库
     * @param fliggy_hotel_info
     * @return
     */
    public String xhotel_add(Fliggy_hotel_info fliggy_hotel_info){
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelAddRequest req = new XhotelAddRequest();
        //必填信息：酒店id，酒店名，城市，酒店地址，酒店电话
        req.setOuterId(fliggy_hotel_info.getOuter_id());
        req.setName(fliggy_hotel_info.getHotel_name());
        req.setDomestic(1L);
        req.setCountry(fliggy_hotel_info.getCountry());
        req.setCity(fliggy_hotel_info.getCity().longValue());
        if(fliggy_hotel_info.getAddress().length() > 120){
            req.setAddress(fliggy_hotel_info.getAddress().substring(0,120));
        }else{
            req.setAddress(fliggy_hotel_info.getAddress());
        }
//        req.setLongitude(fliggy_hotel_info.getLongitude());
//        req.setLatitude(fliggy_hotel_info.getLatitude());
        req.setPositionType("G");
        req.setTel(fliggy_hotel_info.getTel());
        req.setVendor("DOTW");
        req.setNameE(fliggy_hotel_info.getHotel_name());
//        req.setSupplier("DOTW");
        XhotelAddResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.toJSONString(rsp.getBody());
        if(rsp.getMsg() != null && !rsp.getMsg().equals("")){
            res = rsp.getMsg();
        }
        return res;
    }

    /**
     * 新增房型信息入飞猪标准库
     * @param fliggy_roomType_info
     * @return
     */
    public String xRoomType_add(Fliggy_roomType_info fliggy_roomType_info){
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeAddRequest req = new XhotelRoomtypeAddRequest();
        //必填信息：房型id，房型名称，房型英文名，床型，所属酒店id
        req.setOuterId(fliggy_roomType_info.getOuter_id());
        req.setName(fliggy_roomType_info.getName_final());
        req.setBedType(fliggy_roomType_info.getBed_type());
        req.setOutHid(fliggy_roomType_info.getOut_hid());
        req.setVendor("DOTW");
        req.setNameE(fliggy_roomType_info.getName_final());
        XhotelRoomtypeAddResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.toJSONString(rsp.getBody());
        if(rsp.getMsg() != null && !rsp.getMsg().equals("")){
            res = rsp.getMsg();
        }
        return res;
    }

    /**
     * 根据酒店id查询酒店信息
     * @param hid
     * @return
     */
    public JSONObject xHotelSearch(String hid){
        JSONObject res = null;
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelGetRequest req = new XhotelGetRequest();
        req.setOuterId(hid);
        req.setVendor("DOTW");
        XhotelGetResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.parseObject(rsp.getBody());
//        System.out.println(rsp.getBody());

        return res;
    }

    public JSONObject xRoomSearch(String rid){
        JSONObject res = null;
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeGetRequest req = new XhotelRoomtypeGetRequest();
        req.setOuterId(rid);
        req.setVendor("DOTW");
        XhotelRoomtypeGetResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.parseObject(rsp.getBody());
//        System.out.println(rsp.getBody());
        return res;
    }

    /**
     * 根据酒店id删除飞猪酒店信息
     * @param hid
     * @return
     */
    public String xHotelDel(String hid){
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelDeleteRequest req = new XhotelDeleteRequest();
        req.setVendor("DOTW");
        req.setOuterId(hid);
        XhotelDeleteResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = rsp.getBody();
//        System.out.println(rsp.getBody());
        return res;
    }

    /**
     * 根据房型id删除房型
     * @param rid
     * @return
     */
    public String xRoomDel(String rid){
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeDeletePublicRequest req = new XhotelRoomtypeDeletePublicRequest();
        req.setVendor("DOTW");
        req.setOuterRid(rid);
        req.setOperator("DOTW");
        XhotelRoomtypeDeletePublicResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = rsp.getBody();
//        System.out.println(rsp.getBody());
        return res;
    }

}
