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
        // 로그인 성공 후 리다이렉트할 URL
        String redirectUrl = "/home";  // 예시로 /home으로 리다이렉트

        // 로그인 성공 후 리다이렉트
        response.sendRedirect(redirectUrl);
    }

}
