package com.example.OC.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 성공 후 처리할 로직을 구현
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2 로그인 성공 후 공급자 정보를 가져옴
        String provider = (String) authentication.getPrincipal();  // 로그인 공급자 정보
        String redirectUrl = "/home";  // 기본 리다이렉트 URL

        // 로그인 공급자에 맞춰 리다이렉트 URL 변경
        if (provider.contains("naver")) { // 네이버 로그인 후 리다이렉트
            redirectUrl = "/naver-home";
        } else if (provider.contains("apple")) {
            redirectUrl = "/apple-home";  // 애플 로그인 후 리다이렉트
        } else if (provider.contains("kakao")) {
            redirectUrl = "/kakao-home";  // 카카오 로그인 후 리다이렉트
        }

        // 로그인 성공 후 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
