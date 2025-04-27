package com.where.service;

import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.network.request.KakaoLoginRequest;
import com.where.repository.UserRepository;
import com.where.security.jwt.JWTUtil;
import com.where.util.KakaoUserInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final WebClient.Builder webClientBuilder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl("https://kapi.kakao.com").build();
    }

    public String kakaoLogin(KakaoLoginRequest request) {
        KakaoUserInfo kakaoUserInfo = getUserInfo(request.getAccessToken());

        User user = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(kakaoUserInfo.getEmail())
                            .nickName(kakaoUserInfo.getNickname())
                            .profileImage(kakaoUserInfo.getProfileImageUrl())
                            .password("kakao")
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        return jwtUtil.generateToken(
                "user",
                "kakao",
                user.getEmail() != null ? user.getEmail() : "kakao_" + user.getId(),
                user.getRole().name(),
                1000 * 60 * 60 * 24 * 7L // 7일 (ms)
        );
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        try {
            return webClient.get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserInfo.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("카카오 API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
