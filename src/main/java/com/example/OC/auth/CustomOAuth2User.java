package com.example.side.auth;

import com.example.side.user.dto.request.UserOAuth2Dto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserOAuth2Dto userOAuth2Dto;

    public CustomOAuth2User(UserOAuth2Dto userOAuth2Dto) {
        this.userOAuth2Dto = userOAuth2Dto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        /**
         * 구글, 네이버가 제공하는 데이터 형식이 다르기 때문에 사용하지 않음
         */
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> userOAuth2Dto.getRole());

        return authorities;
    }

    @Override
    public String getName() {
        return userOAuth2Dto.getNickname();
    }

    public String getUsername() {
        return userOAuth2Dto.getUid();
    }
}
