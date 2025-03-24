package com.example.OC.network.fcm;

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
}
