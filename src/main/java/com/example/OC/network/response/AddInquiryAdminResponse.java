package com.example.OC.network.response;

import lombok.*;

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

}