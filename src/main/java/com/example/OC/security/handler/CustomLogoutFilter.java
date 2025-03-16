package com.example.OC.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutFilter implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 기본 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // 로그아웃 후 리다이렉트할 URL 설정
        String redirectUrl = "/login?logout=true";  // 로그아웃 후 리다이렉트할 URL

        try {
            // 로그아웃 후 리다이렉트
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
