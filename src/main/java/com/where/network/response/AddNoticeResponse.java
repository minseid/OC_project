package com.where.network.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AddNoticeResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDate date;
}
