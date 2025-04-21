package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendAddCommentDto {

    private Long placeId;

    private Long commentId;

    private String description;
}
