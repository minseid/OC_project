package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class EditMeetingRequest {

    @NotBlank(message = "모임번호를 작성해주세요")
    private Long id;

    private String title;

    private String description;

    private boolean finished;
}
