package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class EditCommentRequest {

    @NotBlank(message = "코멘트정보를 입력해주세요")
    private Long id;

    @NotBlank(message = "유저정보를 입력해주세요")
    private Long userId;

    @NotBlank(message = "코멘트를 입력해주세요")
    private String description;
}
