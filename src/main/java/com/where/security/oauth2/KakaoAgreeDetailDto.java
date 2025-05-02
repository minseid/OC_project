package com.where.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoAgreeDetailDto {

    private String id;
    private boolean agreed;
}
