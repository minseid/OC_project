package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditNoticeRequest {

    private Long id;

    private String title;

    private String content;
}
