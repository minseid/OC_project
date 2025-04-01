package com.where.network.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetParticipantsResponse {

    private Long fromId;

    private String fromName;

    private Long toId;

    private String toName;

    private boolean status;

    private String toImage;
}
