package com.example.OC.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Autowired
    public JwtFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);

                    // 인증 객체 생성
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            new User(username, "", Collections.singletonList(new SimpleGrantedAuthority(role))),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // 토큰 만료 시 로깅 또는 에러 처리
                    logger.info("Token is expired.");
                    // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                }
            } catch (JwtException e) {
                // JWT 처리 오류 발생 시, SecurityContext 초기화 및 로깅
                SecurityContextHolder.clearContext();
                logger.error("JWT processing error: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            }
        }
        chain.doFilter(request, response);
    }


    // Authorization 헤더에서 Bearer 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 부분 추출
        }
        return null;
    }
}
