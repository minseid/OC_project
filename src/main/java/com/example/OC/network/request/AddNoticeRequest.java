package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddNoticeRequest {

    private String title;

    private String content;
}
