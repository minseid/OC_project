package com.where.security.config;

import com.where.repository.UserRepository;
import com.where.security.handler.CustomLogoutFilter;
import com.where.security.handler.CustomLogoutSuccessHandler;
import com.where.security.jwt.*;
import com.where.security.oauth2.AppleOauthService;
import com.where.security.oauth2.CustomOAuth2UserService;
import com.where.security.oauth2.KakaoOauthService;
import com.where.security.oauth2.NaverOauthService;
import com.where.security.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLogoutFilter customLogoutFilter;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtFilter jwtFilter, CustomOAuth2UserService customOAuth2UserService,
                          CustomLogoutFilter customLogoutFilter, CustomLogoutSuccessHandler customLogoutSuccessHandler, CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtFilter = jwtFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customLogoutFilter = customLogoutFilter;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginFilter loginFilter, KaKaoLoginFilter kaKaoLoginFilter, NaverLoginFilter naverLoginFilter, AppleOauthService appleOauthService, AppleLoginFilter appleLoginFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
//                .oauth2Login(oauth -> oauth
//                        .defaultSuccessUrl("/home")
//                        .failureUrl("/login?error=true")
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                )
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(kaKaoLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(naverLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(appleLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter(), UsernamePasswordAuthenticationFilter.class)

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .addLogoutHandler(customLogoutFilter)
                        .logoutSuccessHandler(customLogoutSuccessHandler)

                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Disable for API endpoints
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Enable for others
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                            "default-src 'self'; " +
                                            "script-src 'self' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://kit.fontawesome.com; " +
                                            "style-src 'self' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://fonts.googleapis.com 'unsafe-inline'; " +
                                            "img-src 'self' data: https://audiwhere.codns.com; " +
                                            "font-src 'self' https://fonts.gstatic.com https://ka-f.fontawesome.com https://cdn.jsdelivr.net; " +
                                            "connect-src 'self' https://ka-f.fontawesome.com; " +
                                            "frame-ancestors 'self'; form-action 'self'; " +
                                            "base-uri 'self'; object-src 'none'")
                        )
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .xssProtection(xss -> xss.disable())
                );


        return http.build();
    }
    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://audiwhere.shop"
        ));        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // 세미콜론 허용
        return firewall;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
                builder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return builder.build();
    }


    @Bean
    public LoginFilter loginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {
        return new LoginFilter(authenticationManager, jwtUtil, refreshTokenRepository, userRepository);
    }

    @Bean
    public KaKaoLoginFilter KakakoLoginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            KakaoOauthService kakaoOauthService
    ) {
        return new KaKaoLoginFilter(authenticationManager, jwtUtil, refreshTokenRepository, kakaoOauthService);
    }

    @Bean
    public NaverLoginFilter naverLoginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            NaverOauthService naverOauthService
    ) {
        return new NaverLoginFilter(authenticationManager, jwtUtil, refreshTokenRepository, naverOauthService);
    }

    @Bean
    public AppleLoginFilter appleLoginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshTokenRepository refreshTokenRepository,
            AppleOauthService appleOauthService
    ) {
        return new AppleLoginFilter(authenticationManager, jwtUtil, refreshTokenRepository, appleOauthService);
    }
}