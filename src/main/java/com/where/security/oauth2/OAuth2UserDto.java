package com.where.security.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2UserDto {
    private String uid;
    private String name;
    private String role;
}
