package com.where.network.request;

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

    private Long userId;

}
