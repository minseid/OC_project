package com.where.network.response;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditMeetingResponse {

    private Long id;
    private String title;
    private String description;
    private String link;
    private String image;
}
