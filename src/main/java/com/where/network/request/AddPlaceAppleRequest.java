package com.where.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPlaceAppleRequest {

    @NotNull(message = "모임을 입력해주세요")
    private Long meetingId;

    @NotNull(message = "장소를 공유하는 사람의 유저정보를 입력해주세요")
    private Long userId;

    @NotBlank(message = "장소이름을 입력해주세요")
    private String name;

    @NotBlank(message = "링크를 입력해주세요")
    private String link;

}
