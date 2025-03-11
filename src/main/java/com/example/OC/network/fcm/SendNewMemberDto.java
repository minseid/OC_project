package com.example.OC.network.fcm;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendNewMemberDto {

    private Long meetingId;

    private Long userId;

    private String userName;

    private String userImage;
}
