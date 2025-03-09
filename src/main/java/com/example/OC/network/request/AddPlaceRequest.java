package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.engine.internal.ImmutableEntityEntry;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddPlaceRequest {

    @NotBlank(message = "모임을 입력해주세요")
    private Long meetingId;

    @NotBlank(message = "장소를 공유하는 사람의 유저정보를 입력해주세요")
    private Long userid;

    @NotBlank(message = "장소이름을 입력해주세요")
    private String name;

    @NotBlank(message = "주소를 입력해주세요")
    private String address;

    private String naverLink;
}
