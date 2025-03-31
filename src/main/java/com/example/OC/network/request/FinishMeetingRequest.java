package com.example.OC.network.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinishMeetingRequest {

    @NotNull(message = "모임 정보를 입력해주세요!")
    private Long id;

    @NotNull(message = "유저 정보를 입력해주세요!")
    private Long userId;
}
