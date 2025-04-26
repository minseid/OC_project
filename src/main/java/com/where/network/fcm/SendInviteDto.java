package com.where.network.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class SendInviteDto {

    private Long inviteId;

    private Long meetingId;

    private String meetingImage;

    private String fromNickName;

    private String meetingTitle;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

}
