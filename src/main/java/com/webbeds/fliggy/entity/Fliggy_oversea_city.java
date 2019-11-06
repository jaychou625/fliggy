package com.webbeds.fliggy.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fliggy_oversea_city {
    @Getter
    @Setter
    private String city_id;
    @Getter
    @Setter
    private String city_en_name;
    @Getter
    @Setter
    private String country_id;
    @Getter
    @Setter
    private String country_en_name;
}
