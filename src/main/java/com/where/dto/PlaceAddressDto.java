package com.where.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceAddressDto {

    private String name;

    private String address;

    private float x;

    private float y;

    private String detailAddress;

    private String kakaoLink;

}
