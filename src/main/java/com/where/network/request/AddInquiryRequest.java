package com.where.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddInquiryRequest {

    private String title;

    private String content;

    private Long userId;
}
