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

    private WebClient webClient; // 이건 final 빼야 돼 (생성자 말고 init 메서드에서 설정할거라)

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl("https://kapi.kakao.com").build();
    }

    public String kakaoLogin(KakaoLoginRequest request) {
        KakaoUserInfo kakaoUserInfo = getUserInfo(request.getAccessToken());

        User user = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
//                            .email(kakaoUserInfo.getEmail())
                            .nickName(kakaoUserInfo.getNickname())
//                            .profileImage(kakaoUserInfo.getProfileImageUrl())
                            .password("kakao")
                            .role(UserRole.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        return jwtUtil.generateToken(
                "user",
                "kakao",
                user.getEmail(),
                user.getRole().name(),
                1000 * 60 * 60 * 24 * 7L // 7일 (ms)
        );
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }
}
