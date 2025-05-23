package com.where.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.where.security.oauth2.AppleOauthService;
import com.where.security.oauth2.AppleUserDto;
import com.where.security.oauth2.NaverLoginDto;
import com.where.security.oauth2.NaverOauthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AppleLoginFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/user/apple/login";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleOauthService appleOauthService;

    public AppleLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, AppleOauthService appleOauthService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.appleOauthService = appleOauthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI().equals(LOGIN_PATH)) {
            String appleCode;

            if(request.getHeader("Authorization") != null && request.getHeader("refreshToken") != null) {
                appleCode = request.getHeader("Authorization");
                // 나머지 인증 로직은 동일
                AppleUserDto loginDto = appleOauthService.login(appleCode);
                // targetUser가 null일 때 처리
                if (loginDto.getUser() == null) {
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"Naver login failed: User not found or authentication error\"}");
                    return;
                }
                String username = loginDto.getUser().getEmail();
                try {
                    UserDetails principal = new org.springframework.security.core.userdetails.User(username, loginDto.getUser().getPassword(), AuthorityUtils.createAuthorityList(loginDto.getUser().getRole().toString()));
                    Authentication authResult = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
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
                    response.getWriter().write("{\"userId\": " + loginDto.getUser().getId() + ", \"signUp\": " + loginDto.isSignUp() + "\"\n}");
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