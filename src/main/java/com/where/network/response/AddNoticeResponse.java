package com.where.network.response;

import lombok.*;

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
}
