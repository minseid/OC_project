package com.example.OC.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class LoginFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/login";

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public LoginFilter(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().equals(LOGIN_PATH)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            try {
                // AuthenticationManager를 통해 인증 시도
                Authentication authResult = authenticationManager.authenticate(authenticationToken);

                // 인증 성공 시 SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authResult);

                // 응답 처리 (예: JSON 반환)
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Login successful\"}");
            } catch (Exception e) {
                // 인증 실패 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid credentials\"}");
                return;
            }
        } else {
            chain.doFilter(request, response); // 다른 요청은 다음 필터로 전달
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // /login 경로만 필터링 대상으로 설정
        return !request.getRequestURI().equals(LOGIN_PATH);
    }
}
