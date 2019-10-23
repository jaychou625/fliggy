package com.webbeds.fliggy.entity.search_dotw;

import com.alibaba.fastjson.JSONObject;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Search_info {
    @Getter @Setter
    List<JSONObject> havePriceHotelJustOneDay;
    @Getter @Setter
    List<JSONObject> noPriceHotelJustOneDay;
    @Getter @Setter
    List<JSONObject> havePriceHotel;
    @Getter @Setter
    List<JSONObject> noPriceHotel;
    @Getter @Setter
    List<JSONObject> havePriceHotelFullDay;
    @Getter @Setter
    List<JSONObject> noPriceHotelFullDay;
    @Getter @Setter
    List<String> noPriceHid;
    @Getter @Setter
    String account;
    @Getter @Setter
    String password;
    @Getter @Setter
    String accountId;
    @Getter @Setter
    String path;
    @Getter @Setter
    String version;
    @Getter @Setter
    String fromDate;
    @Getter @Setter
    String toDate;
    @Getter @Setter
    List<String> mark;//用于标记需要删除的酒店id
    @Getter @Setter
    Integer requestCount;//记录请求数量
}
