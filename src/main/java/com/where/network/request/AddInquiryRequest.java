package com.where.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddInquiryRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long userId;
}
