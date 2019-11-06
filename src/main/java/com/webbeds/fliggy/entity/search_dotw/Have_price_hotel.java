package com.webbeds.fliggy.entity.search_dotw;

import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Have_price_hotel {
    @Getter
    @Setter
    String hotel_id;
    @Getter
    @Setter
    String hotel_name;
    @Getter
    @Setter
    String room_id;
    @Getter
    @Setter
    String room_name;
    @Getter
    @Setter
    String check_in;
    @Getter
    @Setter
    String check_out;
    @Getter
    @Setter
    String promotion;
    @Getter
    @Setter
    Map<String, List<Room_rate>> room_rate;
//    @Getter @Setter
//    String meal;
//    @Getter @Setter
//    String total_price;
//    @Getter @Setter
//    String cancel_before_date;
//    @Getter @Setter
//    String cancel_after_date;
}
