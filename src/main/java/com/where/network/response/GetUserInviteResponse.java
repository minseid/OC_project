package com.where.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserInviteResponse {

    private Long inviteId;

    private Long meetingId;

    private String meetingImage;

    private String fromNickName;

    private String meetingTitle;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

}
