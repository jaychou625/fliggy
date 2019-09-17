package com.webbeds.fliggy.entity.Fliggy_interface;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class City_coordinates {
    @Getter @Setter private Long countryId;
    @Getter @Setter private String latitude;
    @Getter @Setter private String longitude;
    @Getter @Setter private String outerId;
}
