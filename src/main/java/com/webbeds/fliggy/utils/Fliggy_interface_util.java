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

import java.util.ArrayList;
import java.util.List;

/**
 * 飞猪接口调用工具类
 */
@Slf4j
public class Fliggy_interface_util {

    private String vendor = "DOTW";

    //account1接口调用地址
    private final String url = "http://gw.api.taobao.com/router/rest";
    //appkey
    private final String appkey = "24823522";
    //secret
    private final String secret = "91c56c1988556d2a8c053c730f74e286";
    //sessionKey
    private final String sessionKey = "61007128f7d3f1fa159513fe3eaeadd6b87bb347f90242c3309205637";

    //account2接口调用地址
//    private final String url = "http://gw.api.taobao.com/router/rest";
//    //appkey
//    private final String appkey = "24823522";
//    //secret
//    private final String secret = "91c56c1988556d2a8c053c730f74e286";
//    //sessionKey
//    private final String sessionKey = "6101d13912ebf414280a518859d671611acd320c55a7b5a3417495593";
//
//    //沙箱接口调用地址
//    private final String urlS = "http://gw.api.tbsandbox.com/router/rest";
//    //appkey
//    private final String appkeyS = "1024823522";
//    //secret
//    private final String secretS = "sandbox988556d2a8c053c730f74e286";
//    //sessionKey
//    private final String sessionKeyS = "61028252845fcd5ace28881ff5ee2301313dcfa6f6456423686530402";
//
//    //account2沙箱接口调用地址
//    private final String urlS2 = "http://gw.api.tbsandbox.com/router/rest";
//    //appkey
//    private final String appkeyS2 = "1024713184";
//    //secret
//    private final String secretS2 = "sandbox88edd90e462a79ed5cdbf2154";
//    //sessionKey
//    private final String sessionKeyS2 = "6101202c1d1d06f080e67019948c9145cb00d93064f83f365753805";


    /**
     * 根据酒店经纬度添加酒店位置信息
     *
     * @param city_coordinates
     * @return
     */
    public String city_coordinates_batch_upload(City_coordinates city_coordinates) {
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
        if (str.indexOf("xhotel_city_coordinates_batch_upload_response") != -1) {
            str = obj.getJSONObject("xhotel_city_coordinates_batch_upload_response").getString("batch_id");
        } else {
            str = null;
        }
        return str;
    }

    /**
     * 根据批次号获取酒店所在城市id
     *
     * @param batch_id
     * @return
     */
    public String coordinates_batch_download(String batch_id) {
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
        String cityCode = "0";
        if (JSON.parseObject(rsp.getBody()).getJSONObject("xhotel_city_coordinates_batch_download_response").getJSONObject("coordinate_list").getJSONArray("coordinate").getJSONObject(0).getString("city") == null) {
            cityCode = "0";
        } else {
            cityCode = JSON.parseObject(rsp.getBody()).getJSONObject("xhotel_city_coordinates_batch_download_response").getJSONObject("coordinate_list").getJSONArray("coordinate").getJSONObject(0).getString("city");
        }
        return cityCode;
    }

    /**
     * 新增酒店信息入飞猪标准库
     *
     * @param fliggy_hotel_info
     * @return
     */
    public String xhotel_add(Fliggy_hotel_info fliggy_hotel_info) {
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelAddRequest req = new XhotelAddRequest();
        //必填信息：酒店id，酒店名，城市，酒店地址，酒店电话
        req.setOuterId(fliggy_hotel_info.getOuter_id());
        req.setName(fliggy_hotel_info.getHotel_name());
        req.setDomestic(1L);
        req.setCountry(fliggy_hotel_info.getCountry());
        req.setCity(fliggy_hotel_info.getCity().longValue());
        if (fliggy_hotel_info.getAddress().length() > 120) {
            req.setAddress(fliggy_hotel_info.getAddress().substring(0, 120));
        } else {
            req.setAddress(fliggy_hotel_info.getAddress());
        }
//        req.setLongitude(fliggy_hotel_info.getLongitude());
//        req.setLatitude(fliggy_hotel_info.getLatitude());
        req.setPositionType("G");
        req.setTel(fliggy_hotel_info.getTel());
        req.setVendor(vendor);
        req.setNameE(fliggy_hotel_info.getHotel_name());
        req.setSupplier(vendor);
        XhotelAddResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.toJSONString(rsp.getBody());
        if (rsp.getMsg() != null && !rsp.getMsg().equals("")) {
            res = rsp.getSubMsg();
        }
        return res;
    }


    /**
     * 更新酒店信息入飞猪标准库,status:-1 删除酒店（慎用），-2 停售
     *
     * @param fliggy_hotel_info
     * @return
     */
    public String xhotel_update(Fliggy_hotel_info fliggy_hotel_info) {
        String res = "";
        byte status = -2;
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelUpdateRequest req = new XhotelUpdateRequest();
        //必填信息：酒店id，酒店名，城市，酒店地址，酒店电话
        req.setOuterId(fliggy_hotel_info.getOuter_id());
        req.setName(fliggy_hotel_info.getHotel_name());
        req.setDomestic(1L);
        req.setCountry(fliggy_hotel_info.getCountry());
        req.setCity(fliggy_hotel_info.getCity().longValue());
        if (fliggy_hotel_info.getAddress().length() > 120) {
            req.setAddress(fliggy_hotel_info.getAddress().substring(0, 120));
        } else {
            req.setAddress(fliggy_hotel_info.getAddress());
        }
//        req.setLongitude(fliggy_hotel_info.getLongitude());
//        req.setLatitude(fliggy_hotel_info.getLatitude());
        req.setPositionType("G");
        req.setTel(fliggy_hotel_info.getTel());
        req.setVendor(vendor);
        req.setNameE(fliggy_hotel_info.getHotel_name());
        req.setSupplier(vendor);
        req.setStatus(status);
        System.out.print("请求报文体：" + JSON.toJSONString(req));
        XhotelUpdateResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.toJSONString(rsp.getBody());
        if (rsp.getMsg() != null && !rsp.getMsg().equals("")) {
            res = rsp.getSubMsg();
        }
        return res;
    }

    /**
     * 新增房型信息入飞猪标准库
     *
     * @param fliggy_roomType_info
     * @return
     */
    public String xRoomType_add(Fliggy_roomType_info fliggy_roomType_info) {
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeAddRequest req = new XhotelRoomtypeAddRequest();
        //必填信息：房型id，房型名称，房型英文名，床型，所属酒店id
        req.setOuterId(fliggy_roomType_info.getOuter_id());
        req.setName(fliggy_roomType_info.getName_final());
        System.out.println(fliggy_roomType_info.getBed_type());
        req.setBedType(fliggy_roomType_info.getBed_type());
//        if(fliggy_roomType_info.getName_final().indexOf("Double") != -1 || fliggy_roomType_info.getName_final().indexOf("DOUBLE") != -1 || fliggy_roomType_info.getName_final().indexOf("double") != -1){
//            req.setBedType("大床");
//        }else if(fliggy_roomType_info.getName_final().indexOf("Twin") != -1 || fliggy_roomType_info.getName_final().indexOf("twin") != -1 || fliggy_roomType_info.getName_final().indexOf("TWIN") != -1){
//            req.setBedType("双床");
//        }else{
//            req.setBedType(fliggy_roomType_info.getBed_type());
//        }
        //临时增加，后续确认逻辑删除
        req.setBedType(fliggy_roomType_info.getBed_type());
        req.setOutHid(fliggy_roomType_info.getOut_hid());
        req.setVendor(vendor);
        req.setNameE(fliggy_roomType_info.getName_final());
        XhotelRoomtypeAddResponse rsp = null;
        System.out.println(req.toString());
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        res = JSON.toJSONString(rsp.getBody());
        System.out.println(res);
        if (rsp.getMsg() != null && !rsp.getMsg().equals("")) {
            res = rsp.getSubMsg();
        }
        return res;
    }

    /**
     * 根据酒店id查询酒店信息
     *
     * @param hid
     * @return
     */
    public JSONObject xHotelSearch(String hid) {
        JSONObject res = null;
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelGetRequest req = new XhotelGetRequest();
        req.setOuterId(hid);
        req.setVendor(vendor);
        XhotelGetResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (rsp.getBody().indexOf("!DOCTYPE html") != -1) {
            System.out.println(rsp.getBody());
        }
        res = JSON.parseObject(rsp.getBody());

        return res;
    }

    public JSONObject xRoomSearch(String rid) {
        JSONObject res = null;
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeGetRequest req = new XhotelRoomtypeGetRequest();
        req.setOuterId(rid);
        req.setVendor(vendor);
        XhotelRoomtypeGetResponse rsp = null;
        try {
            rsp = client.execute(req, sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (rsp.getBody().indexOf("!DOCTYPE html") != -1) {
            System.out.println(rsp.getBody());
        }
        res = JSON.parseObject(rsp.getBody());
        return res;
    }

    /**
     * 根据酒店id删除飞猪酒店信息
     *
     * @param hid
     * @return
     */
    public String xHotelDel(String hid) {
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelDeleteRequest req = new XhotelDeleteRequest();
        req.setVendor(vendor);
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
     *
     * @param rid
     * @return
     */
    public String xRoomDel(String rid) {
        String res = "";
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        XhotelRoomtypeDeletePublicRequest req = new XhotelRoomtypeDeletePublicRequest();
        req.setVendor(vendor);
        req.setOuterRid(rid);
        req.setOperator(vendor);
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
