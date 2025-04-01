package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEditMeetingDto {

    private Long meetingId;

    private String title;

    private String description;

    private String image;

    private boolean finished;
}
