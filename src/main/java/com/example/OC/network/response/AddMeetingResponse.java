package com.example.OC.network.response;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddMeetingResponse {

    private Long id;
    private String title;
    private String description;
    private String link;
    private String image;
}
