package com.where.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteOkResponse {

    private Long id;
    private String title;
    private String description;
    private String link;
    private String image;
    private boolean finished;
    private LocalDateTime createdAt;
}
