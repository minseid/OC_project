package com.example.OC.network.fcm;

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
