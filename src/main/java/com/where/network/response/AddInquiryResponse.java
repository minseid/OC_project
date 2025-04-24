package com.where.network.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddInquiryResponse {

    private Long id;

    private String title;

    private String content;

    private List<String> images;

    private boolean answered;

    private String answerContent;

    private LocalDate inquiryDate;

}