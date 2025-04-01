package com.where.network.fcm;

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
