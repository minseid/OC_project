package com.example.OC.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    // CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 설정
                .allowedOrigins("http://localhost:3000", "https://your-frontend-domain.com")  // 허용할 도메인 추가
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드 설정
                .allowedHeaders("*")  // 허용할 헤더 설정
                .allowCredentials(true)  // 인증 정보를 포함한 요청 허용 (쿠키 등)
                .maxAge(3600);  // CORS 요청을 캐시하는 시간 (초 단위)
    }
}
