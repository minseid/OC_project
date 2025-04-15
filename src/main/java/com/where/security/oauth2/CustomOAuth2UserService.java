package com.where.security.oauth2;

import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@Primary
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 요청 처리");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 사용자 정보: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao", "naver" 등

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthUserInfo userInfo = extractUserInfo(registrationId, attributes);

        // 사용자 정보 처리 로직
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> createUser(userInfo, registrationId));

        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("provider", registrationId);
        customAttributes.put("email", userInfo.getEmail());
        customAttributes.put("name", userInfo.getName());
        customAttributes.put("profileImage", userInfo.getProfileImage());

        Collection<OAuth2UserAuthority> authorities = Collections.singleton(
                new OAuth2UserAuthority(user.getRole().getKey(), customAttributes)
        );

        return new DefaultOAuth2User(authorities, customAttributes, "email");
    }

    private User createUser(OAuthUserInfo userInfo, String provider) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .nickName(userInfo.getName())  // 닉네임은 일단 이름과 동일하게
                .profileImage(userInfo.getProfileImage())
                .provider(provider)
                .role(UserRole.USER)
                // 소셜 로그인은 실제 비밀번호가 없으므로 랜덤한 문자열을 암호화하여 저장
                .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID().toString()))
                .build();

        return userRepository.save(user);
    }

    private OAuthUserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return extractKakaoUserInfo(attributes);
        } else if ("naver".equals(registrationId)) {
            return extractNaverUserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        }
    }

    private OAuthUserInfo extractKakaoUserInfo(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthUserInfo.builder()
                .email((String) kakaoAccount.get("email"))
                .name((String) profile.get("nickname"))
                .profileImage((String) profile.get("profile_image_url"))
                .build();
    }

    private OAuthUserInfo extractNaverUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthUserInfo.builder()
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .profileImage((String) response.get("profile_image"))
                .build();
    }
}