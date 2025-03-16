package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikePlaceRequest {

    private Long id;

    private boolean like;

}
