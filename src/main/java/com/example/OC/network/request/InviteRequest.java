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

    @NotNull(message = "모임 정보를 입력해주세요!")
    private Long meetingId;

    @NotNull(message = "초대하는 유저 정보를 입력해주세요!")
    private Long fromId;

    @NotNull(message = "초대받는 유저 정보를 입력해주세요!")
    private Long toId;

}
