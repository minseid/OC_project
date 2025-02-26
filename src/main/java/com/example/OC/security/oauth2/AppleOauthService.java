package com.example.OC.security.oauth2;

import com.example.OC.entity.User;
import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleOauthService {

    private final UserRepository userRepository;
    private final String appleClientId = "YOUR_APPLE_CLIENT_ID";
    private final String appleClientSecret = "YOUR_APPLE_CLIENT_SECRET";
    private final String appleRedirectUri = "YOUR_REDIRECT_URI";
    private final String appleAuthUrl = "https://appleid.apple.com/auth/token";

    // Apple OAuth2 인증 후 사용자 정보를 가져오는 메서드
    public User getAppleUserInfo(String code) {
        String tokenUrl = UriComponentsBuilder.fromHttpUrl(appleAuthUrl)
                .queryParam("client_id", appleClientId)
                .queryParam("client_secret", appleClientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", appleRedirectUri)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        // Apple OAuth2 토큰을 받아오기 위한 API 호출
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(tokenUrl, null, Map.class);

        // 토큰을 이용해 사용자 정보 얻기 (예시)
        String accessToken = (String) response.get("access_token");

        // Apple API를 호출하여 사용자 정보 받기
        String userInfoUrl = "https://api.apple.com/userinfo";
        Map<String, Object> userInfo = restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, Map.class);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // 이메일로 사용자 정보 찾기
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // 없으면 새로운 사용자 생성
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            return userRepository.save(newUser);
        });

        return user;
    }
}
