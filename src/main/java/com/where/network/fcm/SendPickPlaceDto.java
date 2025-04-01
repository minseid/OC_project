package com.where.network.fcm;

import com.where.constant.PlaceStatus;
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
