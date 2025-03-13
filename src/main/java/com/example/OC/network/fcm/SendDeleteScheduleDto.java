package com.example.OC.network.fcm;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendDeleteScheduleDto {

    private Long meetingId;
}
