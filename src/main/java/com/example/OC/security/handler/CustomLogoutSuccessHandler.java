package com.example.OC.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 로그아웃 후 리다이렉트 URL 설정
        String redirectUrl = "/login?logout=true";  // 기본적으로 로그인 페이지로 리다이렉트

        // 소셜 로그인 후 로그아웃 처리
        String provider = (authentication != null) ? (String) authentication.getPrincipal() : null;

        if (provider != null) {
            if (provider.equals("naver")) {
                // 네이버 로그아웃 처리 후 리다이렉트 URL
                redirectUrl = "/naver-logout-success";
            } else if (provider.equals("apple")) {
                // 애플 로그아웃 처리 후 리다이렉트 URL
                redirectUrl = "/apple-logout-success";
            } else if (provider.equals("kakao")) {
                // 카카오 로그아웃 처리 후 리다이렉트 URL
                redirectUrl = "/kakao-logout-success";

            }
        }

        // 로그아웃 후 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
