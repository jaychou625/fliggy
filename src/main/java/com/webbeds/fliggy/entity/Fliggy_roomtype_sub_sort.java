package com.webbeds.fliggy.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fliggy_roomtype_sub_sort {
    @Setter
    @Getter
    private Integer id;
    @Setter
    @Getter
    private String outer_rid;
    @Setter
    @Getter
    private String outer_hid;
    @Setter
    @Getter
    private String sub_id;
    @Setter
    @Getter
    private String sub_str;
}
