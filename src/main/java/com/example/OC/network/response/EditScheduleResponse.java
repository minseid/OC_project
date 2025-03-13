package com.example.OC.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditScheduleResponse {

    private Long meetingId;

    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
}
