package com.where.network.response;

import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}
