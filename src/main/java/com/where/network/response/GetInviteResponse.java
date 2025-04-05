package com.where.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class GetInviteResponse {

    private Long meetingId;

    private String title;

    private String image;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;
}
