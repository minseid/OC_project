package com.where.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 성공 후 처리할 로직을 구현
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/home";  // 기본 리다이렉트 URL
        String provider = null;

        // OAuth2 로그인 성공 후 공급자 정보를 가져옴
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            // provider 정보 확인
            if (attributes.containsKey("provider")) {
                provider = (String) attributes.get("provider");
            }
        }

        // 로그인 공급자에 맞춰 리다이렉트 URL 변경
        if (provider != null) {
            if (provider.equals("naver")) {
                redirectUrl = "/naver-home";
            } else if (provider.equals("apple")) {
                redirectUrl = "/apple-home";
            } else if (provider.equals("kakao")) {
                redirectUrl = "/kakao-home";
            }
        }

        // 로그인 성공 후 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
