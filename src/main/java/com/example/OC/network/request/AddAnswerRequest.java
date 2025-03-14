package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddAnswerRequest {

    private Long inquiryId;

    private String answerContent;
}
