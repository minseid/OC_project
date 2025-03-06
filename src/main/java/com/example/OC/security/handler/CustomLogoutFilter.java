package com.example.OC.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomLogoutFilter implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 기본 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // 로그아웃 후 추가 처리 (예: 특정 URL로 리다이렉트)
        String redirectUrl = "/login?logout=true";  // 로그아웃 후 리다이렉트할 URL
        UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
                .replacePath(redirectUrl)  // 로그아웃 후 리다이렉트 URL 설정
                .build().toUriString();

        try {
            response.sendRedirect(redirectUrl);  // 리다이렉트 처리
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
