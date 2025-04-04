package com.where.network.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentResponse {

    private Long commentId;

    private String description;
}
