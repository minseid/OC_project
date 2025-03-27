package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AddMeetingRequest {

    @NotBlank(message = "모임명을 작성해주세요")
    private String title;

    @NotNull(message = "초대자를 작성해주세요")
    private Long fromId;

    @NotBlank(message = "모임설명을 작성해주세요")
    private String description;

    private List<Long> participants;
}
