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
public class AddInquiryAdminResponse {

    private Long id;

    private Long userId;

    private String userName;

    private String title;

    private String content;

    private List<String> images;

    private boolean answered;

    private String answerContent;

    private LocalDate inquiryDate;

    private LocalDate answerDate;

}