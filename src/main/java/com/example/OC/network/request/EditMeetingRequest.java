package com.example.OC.network.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class EditMeetingRequest {

    @NotBlank(message = "모임번호를 작성해주세요")
    private Long id;

    @NotBlank(message = "모임명을 작성해주세요")
    private String title;

    @NotBlank(message = "초대자를 작성해주세요")
    private Long fromId;

    @NotBlank(message = "모임설명을 작성해주세요")
    private String description;

    private String image;
}
