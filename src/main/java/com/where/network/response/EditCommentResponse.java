package com.where.network.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EditCommentResponse {

    private Long commentId;

    private String description;
}
