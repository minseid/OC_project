package com.example.OC.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMeetingsResponse {

    private Long id;
    private String title;
    private String description;
    private String link;
    private String image;
    private boolean finished;
}
