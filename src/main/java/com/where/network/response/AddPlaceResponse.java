package com.where.network.response;

import com.where.constant.PlaceStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AddPlaceResponse {

    private Long id;

    private String naverLink;

    private String kakaoLink;

    private String name;

    private String address;

    private int likes;

    private boolean myLike;

    private PlaceStatus placeStatus;

    private boolean together;
}
