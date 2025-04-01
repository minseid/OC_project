package com.where.network.fcm;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendAddMemberDto {

    private Long meetingId;

    private Long userId;

    private String userName;

    private String userImage;
}
