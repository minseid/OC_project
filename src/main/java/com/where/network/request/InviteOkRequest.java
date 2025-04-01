package com.where.network.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteOkRequest {

    @NotNull(message = "초대정보를 입력해주세요!")
    private Long id;
}
