package com.where.network.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KakaoMapApiResponse {

    //위도 latitude
    private int lat;

    //경도 longitude
    private int lng;
}
