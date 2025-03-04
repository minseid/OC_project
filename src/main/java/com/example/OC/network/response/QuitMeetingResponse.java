package com.example.OC.network.response;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuitMeetingResponse {

    private Long id;
    private Long userId;
}
