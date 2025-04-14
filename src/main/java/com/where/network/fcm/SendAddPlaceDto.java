package com.where.network.fcm;

import com.where.constant.PlaceStatus;
import lombok.*;

import java.util.List;

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

    private List<Long> likes;

    private PlaceStatus placeStatus;

    private String naverLink;

    private String kakaoLink;

}
