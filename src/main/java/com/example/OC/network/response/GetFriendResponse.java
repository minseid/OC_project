package com.example.OC.network.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFriendResponse {

    private Long friendId;

    private String friendName;

    private String friendImage;

    private List<MeetingForFriendResponse> meetingDetail;
}
