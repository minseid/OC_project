package com.example.OC.network.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonPlaceRequest {

    @NotBlank(message = "장소를 입력해주세요.")
    private Long placeId;

    @NotBlank(message = "모임을 입력해주세요.")
    private Long meetingId;
}
