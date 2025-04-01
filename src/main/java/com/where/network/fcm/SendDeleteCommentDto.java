package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendDeleteCommentDto {

    private Long commentId;
}
