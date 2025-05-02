package com.where.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetKakaoAccessTokenRequest {

    private String grant_type;

    private String client_id;

    private String refresh_token;
}
