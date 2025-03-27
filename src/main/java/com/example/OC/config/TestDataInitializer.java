package com.example.OC.config;

import com.example.OC.constant.UserRole;
import com.example.OC.entity.User;
import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class TestDataInitializer {
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            User testUser = User.builder()
                    .email("test@example.com")
                    .password(passwordEncoder.encode("Test@1234"))
                    .nickName("테스트유저")
                    .profileImage("https://example.com/default-profile.png")
                    .role(UserRole.USER)

                    .build();

            if (userRepository.findByEmail(testUser.getEmail()).isEmpty()) {
                userRepository.save(testUser);
                System.out.println("테스트 유저가 DB에 삽입되었습니다: " + testUser);
            } else {
                System.out.println("테스트 유저가 이미 존재합니다.");
            }
        };
    }
}
