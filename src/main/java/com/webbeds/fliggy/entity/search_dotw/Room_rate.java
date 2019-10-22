package com.webbeds.fliggy.entity.search_dotw;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Room_rate {
    @Getter @Setter
    String meal;
    @Getter @Setter
    String price;
    @Getter @Setter
    String cancel_before;
    @Getter @Setter
    String cancel_after;
}
