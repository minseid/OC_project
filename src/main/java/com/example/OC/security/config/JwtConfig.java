package com.example.OC.security.config;

import com.example.OC.security.jwt.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil("7AED6A4D99742D76F9386D5CE6F147759122E529047285490284CD6FC6DC6A3F");
    }
}
