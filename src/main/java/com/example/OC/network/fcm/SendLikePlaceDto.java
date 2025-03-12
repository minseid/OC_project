package com.example.OC.network.fcm;

import com.example.OC.constant.PlaceStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendLikePlaceDto {

    private Long placeId;

    private long likeCount;
}
