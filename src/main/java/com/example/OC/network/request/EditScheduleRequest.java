package com.example.OC.network.request;

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
public class EditScheduleRequest {

    @NotNull(message = "일정정보를 입력해주세요!")
    private Long ScheduleId;

    @NotNull(message = "날짜를 입력해주세요!")
    private LocalDate date;

    @NotNull(message = "시각을 입력해주세요!")
    private LocalTime time;

}
