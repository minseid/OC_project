package com.where.security.handler;

import com.where.security.jwt.RefreshToken;
import com.where.security.jwt.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutFilter implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public CustomLogoutFilter(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // JWT 토큰 무효화 로직 추가
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            // 리프레시 토큰 삭제
            refreshTokenRepository.findById(username).ifPresent(refreshTokenRepository::delete);
        }

        // 기본 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
}