package com.where.network.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingForFriendResponse {

    private Long meetingId;

    private String meetingImage;

    private String meetingName;

    private String description;

    private LocalDate date;

}
