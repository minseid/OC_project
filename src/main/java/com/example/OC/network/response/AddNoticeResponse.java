package com.example.OC.network.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AddNoticeResponse {

    private Long noticeId;

    private String title;

    private String content;
}
