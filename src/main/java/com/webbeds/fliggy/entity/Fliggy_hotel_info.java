package com.webbeds.fliggy.entity;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fliggy_hotel_info {

    //必填
    @Getter
    @Setter
    private String outer_id;
    //必填
    @Getter
    @Setter
    private String hotel_name;
    @Getter
    @Setter
    private String used_name;
    @Getter
    @Setter
    private Integer domestic;
    @Getter
    @Setter
    private String country;
    @Getter
    @Setter
    private Integer province;
    //必填
    @Getter
    @Setter
    private Integer city;
    @Getter
    @Setter
    private Integer district;
    @Getter
    @Setter
    private String business;
    //必填
    @Getter
    @Setter
    private String address;
    @Getter
    @Setter
    private String longitude;
    @Getter
    @Setter
    private String latitude;
    @Getter
    @Setter
    private String position_type;
    //必填
    @Getter
    @Setter
    private String tel;
    @Getter
    @Setter
    private String extend;
    @Getter
    @Setter
    private Integer shid;
    @Getter
    @Setter
    private String vendor = "DOTW";
    @Getter
    @Setter
    private String star;
    @Getter
    @Setter
    private String opening_time;
    @Getter
    @Setter
    private String decorate_time;
    @Getter
    @Setter
    private String floors;
    @Getter
    @Setter
    private Integer rooms;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String hotel_policies;
    @Getter
    @Setter
    private String hotel_facilities;
    @Getter
    @Setter
    private String service;
    @Getter
    @Setter
    private String room_facilities;
    @Getter
    @Setter
    private String pics;
    @Getter
    @Setter
    private String brand;
    @Getter
    @Setter
    private String postal_code;
    @Getter
    @Setter
    private String booking_notice;
    @Getter
    @Setter
    private String credit_card_types;
    @Getter
    @Setter
    private String orbit_track;
    @Getter
    @Setter
    private String name_e;
    @Getter
    @Setter
    private String supplier;
    @Getter
    @Setter
    private String settlement_currency;
    @Getter
    @Setter
    private String standard_amuse_facilities;
    @Getter
    @Setter
    private String standard_room_facilities;
    @Getter
    @Setter
    private String standard_hotel_service;
    @Getter
    @Setter
    private String standard_hotel_facilities;
    @Getter
    @Setter
    private String standard_booking_notice;
    @Getter
    @Setter
    private String batch_id;
    @Getter
    @Setter
    private String state;
    @Getter
    @Setter
    private Date insertDate;
    @Getter
    @Setter
    private String error_msg;
    @Getter
    @Setter
    private String mapping;
    @Getter
    @Setter
    private String have_price;
    @Getter
    @Setter
    private Date have_price_date;


}
