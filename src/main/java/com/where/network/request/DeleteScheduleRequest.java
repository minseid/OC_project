package com.where.network.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

@Data
@AllArgsConstructor
@NonEmpty
@Builder
public class DeleteScheduleRequest {

    @NotNull(message = "모임 정보를 입력해주세요!")
    private Long meetingId;

    @NotNull(message = "유저 정보를 입력해주세요!")
    private Long userId;
}
