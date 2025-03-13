package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikePlaceRequest {

    private Long placeId;

    private boolean like;

}
