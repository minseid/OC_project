package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendTogetherPlaceDto {

    private Long placeId;

    private boolean together;

    private String user;
}
