package com.example.OC.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider, OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        return ResponseEntity.ok().build();
    }
}
