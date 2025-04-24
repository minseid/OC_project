package com.where.network.response;

import com.where.entity.TimeBaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private LocalDate answerDate;

    private LocalDate inquiryDate;
}
