package com.example.OC.network.fcm;

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

    private Long userId;

    private String description;
}
