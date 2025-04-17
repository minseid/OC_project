package com.where.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/user/login";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().equals(LOGIN_PATH)) {
            String username = null;
            String password = null;

            // Content-Type 확인
            String contentType = request.getContentType();

            // JSON 요청 처리
            if (contentType != null && contentType.contains("application/json")) {
                // JSON 파싱
                Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);
                username = loginData.get("username");
                password = loginData.get("password");
            }
            // 폼 데이터 처리 (기존 코드)
            else {
                username = request.getParameter("username");
                password = request.getParameter("password");
            }

            // 나머지 인증 로직은 동일
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            try {
                Authentication authResult = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authResult);

                String accessToken = jwtUtil.generateAccessToken(username);
                String refreshToken = jwtUtil.generateRefreshToken(username);

                // 기존 리프레시 토큰이 있으면 삭제
                refreshTokenRepository.findById(username).ifPresent(refreshTokenRepository::delete);

                // Refresh Token 저장
                RefreshToken tokenEntity = new RefreshToken(username, refreshToken);
                refreshTokenRepository.save(tokenEntity);

                // 응답 헤더에 토큰 추가
                response.setHeader("Authorization", "Bearer " + accessToken);
                response.setHeader("Refresh-Token", refreshToken);

                // 성공 응답 (본문에는 토큰 정보 없이 성공 메시지만 포함)
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": true, \"message\": \"로그인 성공\"}");
            } catch (AuthenticationException e) {
                // 기존 인증 실패 처리 유지
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                response.getWriter().write(objectMapper.writeValueAsString(error));
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