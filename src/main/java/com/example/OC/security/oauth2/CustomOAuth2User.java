package com.example.OC.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2UserDto OAuth2UserDto;

    public CustomOAuth2User(com.example.OC.security.oauth2.OAuth2UserDto oAuth2UserDto) {
        OAuth2UserDto = oAuth2UserDto;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> OAuth2UserDto.getRole());

        return authorities;
    }

    @Override
    public String getName() {
        return OAuth2UserDto.getName();
    }
}
