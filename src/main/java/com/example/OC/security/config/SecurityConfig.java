package com.example.OC.security.config;

import com.example.OC.security.jwt.JwtFilter;
import com.example.OC.security.jwt.JwtUtils;
import com.example.OC.security.oauth2.KakaoOauthService;
import com.example.OC.security.oauth2.NaverOauthService; // 네이버 OAuth2 서비스 추가
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtUtils jwtUtils;
    private final KakaoOauthService kakaoOauthService;
    private final NaverOauthService naverOauthService; // 네이버 OAuth2 서비스 추가

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 설정 (비활성화)
                .csrf(csrf -> csrf.disable())

                // 인증 및 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/**").permitAll() // 로그인 관련 URL 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )

                // OAuth2 로그인 설정 (카카오 및 네이버)
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home") // 로그인 성공 시 리다이렉트 URL
                        .failureUrl("/login?error=true") // 로그인 실패 시 리다이렉트 URL
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(kakaoOauthService) // 카카오 사용자 정보 처리 서비스 등록
                                .userService(naverOauthService) // 네이버 사용자 정보 처리 서비스 등록
                        )
                );

        // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 이전에 실행)
        http.addFilterBefore((Filter) jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
