package com.where.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SnsSignupResponse {

    private Long userId;
}
