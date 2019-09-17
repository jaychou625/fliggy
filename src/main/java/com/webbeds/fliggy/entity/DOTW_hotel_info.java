package com.webbeds.fliggy.entity;

import lombok.*;

/**
 * DOTW上新酒店实体类
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DOTW_hotel_info {
    @Setter @Getter private String country;
    @Setter @Getter private String city;
    @Setter @Getter private String hotelCode;
    @Setter @Getter private String hotelName;
    @Setter @Getter private String starRating;
    @Setter @Getter private String reservationTelephone;
    @Setter @Getter private String hotelAddress;
    @Setter @Getter private String latitude;
    @Setter @Getter private String longitude;
    @Setter @Getter private String chainName;
    @Setter @Getter private String brandName;
    @Setter @Getter private String new_Property;
}
