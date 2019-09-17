package com.webbeds.fliggy.entity.FR_hotels;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FR_room_info {
    @Setter @Getter private String room_id;
    @Setter @Getter private String type_id;
    @Setter @Getter private String best_buy;
    @Setter @Getter private String meal_supplement_restriction;
    @Setter @Getter private String hid;
}
