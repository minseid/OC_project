package com.where.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/user/login";

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;  // ⭐ 추가

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;  // ⭐ 주입
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
                Authentication authResult = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authResult);

                String accessToken = jwtUtil.generateAccessToken(username);
                String refreshToken = jwtUtil.generateRefreshToken(username);

                // Refresh Token 저장
                RefreshToken tokenEntity = new RefreshToken(username, refreshToken);
                refreshTokenRepository.save(tokenEntity);

                // 응답 처리
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String jsonResponse = "{"
                        + "\"accessToken\":\"" + accessToken + "\","
                        + "\"refreshToken\":\"" + refreshToken + "\""
                        + "}";

                response.getWriter().write(jsonResponse);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid credentials\"}");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().equals(LOGIN_PATH);
    }
}
