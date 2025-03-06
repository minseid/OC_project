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
        String redirectUrl = "/login?logout=true";  // 로그아웃 후 리다이렉트할 URL

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }


}
