package com.example.OC.network.response;

import com.example.OC.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickPlaceResponse {

    private Long placeId;

    private long likeCount;

    private PlaceStatus placeStatus;
}
