package com.where.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

@Data
@AllArgsConstructor
@NonEmpty
@Builder
public class DeleteScheduleRequest {

    private Long meetingId;
}
