package com.example.OC.network.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EditNoticeResponse {

    private Long id;

    private String title;

    private String content;
}
