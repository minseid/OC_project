package com.example.OC.network.fcm;

import com.example.OC.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendAddPlaceDto {

    private Long meetingId;

    private Long placeId;

    private String placeName;

    private String address;

    private long likeCount;

    private PlaceStatus placeStatus;

    private String naverLink;

    private String kakaoLink;

}
