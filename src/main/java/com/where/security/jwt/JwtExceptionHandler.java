package com.where.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.Map;

@ControllerAdvice
public class JwtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(JwtExceptionHandler.class);

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException e) {
        logger.error("JWT Exception occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token: " + e.getMessage()));
    }

    // 컨트롤러 실행 전에 request 속성 확인하여 토큰 관련 오류 처리
    @ModelAttribute
    public void handleRequestAttributes(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        if (Boolean.TRUE.equals(request.getAttribute("expired"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\"}");
        } else if (Boolean.TRUE.equals(request.getAttribute("invalid"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token: " + request.getAttribute("error") + "\"}");
        }
    }
}