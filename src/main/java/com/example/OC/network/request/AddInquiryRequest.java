package com.example.OC.network.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddInquiryRequest {

    private Long userId;

    private String title;

    private String content;

}
