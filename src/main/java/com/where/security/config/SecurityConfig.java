package com.where.security.config;

import com.where.security.jwt.JwtFilter;
import com.where.security.jwt.LoginFilter;
import com.where.security.oauth2.KakaoOauthService;
import com.where.security.oauth2.NaverOauthService; // 네이버 OAuth2 서비스 추가
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.firewall.StrictHttpFirewall;


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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
//                        .requestMatchers("/api/user/signup").permitAll()
//                        .requestMatchers("/api/user/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home")
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo.userService(kakaoOauthService))
                )
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // 세미콜론 허용
        return firewall;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public LoginFilter loginFilter(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        return new LoginFilter(authenticationManager, passwordEncoder);
    }

}
