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
public class QuitMeetingRequest {

    @NotNull(message = "모임번호를 작성해주세요")
    private Long id;

    @NotNull(message = "사용자id를 작성해주세요")
    private Long userId;
}
