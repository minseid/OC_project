package com.where.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenController(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        try {
            // 1. 토큰 유효성 검사
            String username = jwtUtil.extractUsername(refreshToken);
            RefreshToken savedToken = refreshTokenRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("No refresh token found"));

            // 2. DB에 저장된 토큰과 비교
            if (!savedToken.getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }

            // 3. 토큰 재발급
            String newAccessToken = jwtUtil.generateAccessToken(username);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token invalid or expired"));
        }
    }
}
