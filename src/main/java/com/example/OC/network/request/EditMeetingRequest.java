package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class EditMeetingRequest {

    @NotNull(message = "모임번호를 작성해주세요")
    private Long id;

    private String title;

    private String description;

    @NotNull(message = "유저정보를 입력해주세요")
    private Long userId;
}
