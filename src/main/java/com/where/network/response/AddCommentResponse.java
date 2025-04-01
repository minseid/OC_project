package com.where.network.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentResponse {

    private Long placeId;

    private Long commentId;

    private Long userId;

    private String description;
}
