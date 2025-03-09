package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AddCommentRequest {

    @NotBlank(message = "장소정보를 입력해주세요")
    private Long placeId;

    @NotBlank(message = "유저정보를 입력해주세요")
    private Long userId;

    @NotBlank(message = "코멘트를 입력해주세요")
    private String description;
}
