package com.example.OC.network.fcm;

import com.example.OC.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendPickPlaceDto {

    private Long placeId;

    private PlaceStatus placeStatus;
}
