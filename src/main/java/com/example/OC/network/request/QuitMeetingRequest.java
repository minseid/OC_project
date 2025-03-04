package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class QuitMeetingRequest {

    @NotBlank(message = "모임번호를 작성해주세요")
    private Long id;

    @NotBlank(message = "사용자id를 작성해주세요")
    private Long userId;
}
