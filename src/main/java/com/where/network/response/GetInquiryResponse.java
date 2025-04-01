package com.where.network.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetInquiryResponse {

    private Long id;

    private String title;

    private String content;

    private List<String> images;

    private boolean answered;

    private String answerContent;
}
