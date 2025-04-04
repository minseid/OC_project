package com.where.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMeetingsResponse {

    private Long id;
    private String title;
    private String description;
    private String link;
    private String image;
    private boolean finished;
    private LocalDateTime createdAt;
    private LocalDate scheduleDate;
    private LocalTime scheduleTime;
}
