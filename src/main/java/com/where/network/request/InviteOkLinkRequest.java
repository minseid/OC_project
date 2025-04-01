package com.where.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteOkLinkRequest {

    @NotNull(message = "유저정보를 입력해주세요!")
    private Long userId;

    @NotBlank(message = "모임초대링크를 입력해주세요!")
    private String link;
}
