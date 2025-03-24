package com.example.OC.network.response;

import com.example.OC.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPlaceResponse {

    private Long id;

    private Long meetingId;

    private String naverLink;

    private String kakaoLink;

    private String name;

    private String address;

    private long likeCount;

    private PlaceStatus placeStatus;

    private boolean together;
}
