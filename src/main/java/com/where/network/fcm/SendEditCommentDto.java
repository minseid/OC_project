package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendEditCommentDto {

    private Long commentId;

    private String description;
}
