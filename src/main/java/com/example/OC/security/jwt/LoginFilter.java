package com.example.OC.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 로그인 요청인지 확인 (예: POST /login 요청)
        if (request.getRequestURI().equals("/login") && request.getMethod().equals("POST")) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // 사용자 인증 시도
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            try {
                // AuthenticationManager를 통해 인증을 수행
                authenticationManager.authenticate(authenticationToken);

                // 인증이 성공하면 SecurityContext에 인증 정보 저장
                User user = new User(username, password, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities()));
            } catch (Exception e) {
                // 인증 실패 시
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("Invalid credentials");
                return;
            }
        }

        // 요청을 계속 전달
        chain.doFilter(request, response);
    }
}
