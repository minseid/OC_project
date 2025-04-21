package com.where.network.fcm;

import com.where.constant.PlaceStatus;
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

    private String user;

    private String placeName;

    private String address;

    private int likes;

    private PlaceStatus placeStatus;

    private String naverLink;

    private String kakaoLink;

}
