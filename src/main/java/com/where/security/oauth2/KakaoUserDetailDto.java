package com.where.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoUserDetailDto {

    private KakaoProfileDetailDto profile;
    private String email;
}
