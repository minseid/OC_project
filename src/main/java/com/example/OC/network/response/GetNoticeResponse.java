package com.example.OC.network.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetNoticeResponse {

    private Long noticeId;

    private String title;

    private String content;
}
