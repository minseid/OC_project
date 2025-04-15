package com.where.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${JWT_SECRET}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String extractUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new JwtException("Invalid token: " + e.getMessage());
        }
    }
    public String generateAccessToken(String username) {
        return generateToken("access", "default", username, "ROLE_USER", 1000 * 60 * 15); // 15 minutes
    }

    public String generateRefreshToken(String username) {
        return generateToken("refresh", "default", username, "ROLE_USER", 1000L * 60 * 60 * 24 * 14); // 14 days
    }

    public String generateToken(String category, String loginType, String username, String role, long expirationMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("loginType", loginType)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }
}
