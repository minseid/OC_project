package com.where.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddAnswerRequest {

    private Long id;

    private String answerContent;
}
