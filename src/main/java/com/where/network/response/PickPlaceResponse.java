package com.where.network.response;

import com.where.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickPlaceResponse {

    private Long id;

    private int likes;

    private boolean myLike;

    private PlaceStatus placeStatus;
}
