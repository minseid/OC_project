package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class InviteRequest {

    @NotNull
    private Long meetingId;

    @NotNull
    private Long fromId;

    @NotNull
    private Long toId;

}
