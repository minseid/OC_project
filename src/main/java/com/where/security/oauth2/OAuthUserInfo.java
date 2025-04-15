package com.where.security.oauth2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {
    private String email;
    private String name;
    private String profileImage;
}