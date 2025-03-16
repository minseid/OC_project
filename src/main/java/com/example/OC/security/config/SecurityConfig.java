package com.example.OC.security.config;

import com.example.OC.constant.UserRole;
import com.example.OC.security.jwt.JwtFilter;
import com.example.OC.security.jwt.LoginFilter;
import com.example.OC.security.oauth2.KakaoOauthService;
import com.example.OC.security.oauth2.NaverOauthService; // 네이버 OAuth2 서비스 추가
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;


@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final KakaoOauthService kakaoOauthService;
    @Getter
    private final NaverOauthService naverOauthService;

    public SecurityConfig(JwtFilter jwtFilter, KakaoOauthService kakaoOauthService, NaverOauthService naverOauthService) {
        this.jwtFilter = jwtFilter;
        this.kakaoOauthService = kakaoOauthService;
        this.naverOauthService = naverOauthService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginFilter loginFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth
                        .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home")
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(kakaoOauthService)
                        )
                )
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class) // LoginFilter 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    // 🔹 LoginFilter를 직접 Bean으로 등록하여 순환 참조 해결
    @Bean
    public LoginFilter loginFilter(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        return new LoginFilter(authenticationManager, passwordEncoder);
    }

}
