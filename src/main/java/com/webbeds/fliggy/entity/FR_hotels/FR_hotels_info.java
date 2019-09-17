package com.webbeds.fliggy.entity.FR_hotels;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FR_hotels_info {
    @Setter @Getter private String id;
    @Setter @Getter private String hotel_name;
    @Setter @Getter private String address;
    @Setter @Getter private String accomodation_type;
    @Setter @Getter private String description;
    @Setter @Getter private String adult_only;
    @Setter @Getter private String classification;
    @Setter @Getter private String phone;
    @Setter @Getter private String infant_restrictions;
    @Setter @Getter private String place;
    @Setter @Getter private String geoposition;
    @Setter @Getter private String fax;
    @Setter @Getter private String headline;
    @Setter @Getter private String email;
    @Setter @Getter private String best_buy;
    @Setter @Getter private String features;
    @Setter @Getter private String imgPath;
    @Setter @Getter private String distances;
    @Setter @Getter private String reviews;
    @Setter @Getter private String themes;

}
