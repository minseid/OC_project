package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AddScheduleRequest {

    @NotNull(message = "모임정보를 입력해주세요")
    private Long meetingId;

    @NotNull(message = "날짜를 입력해주세요")
    private LocalDate date;

    @NotNull(message = "시각을 입력해주세요")
    private LocalTime time;

}
