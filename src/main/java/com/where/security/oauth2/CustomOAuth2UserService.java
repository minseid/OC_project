package com.where.security.oauth2;

import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User.getAttributes());

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // ex) "kakao", "naver"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;
        String profileImage = null;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            profileImage = (String) profile.get("profile_image_url");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            profileImage = (String) response.get("profile_image");
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        log.info("OAuth2 user info - provider: {}, name: {}, email: {}, profileImage: {}", registrationId, name, email, profileImage);

        final String finalEmail = email;
        final String finalName = name;
        final String finalProfileImage = profileImage;
        final String finalRegistrationId = registrationId;

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(finalEmail)
                        .name(finalName)
                        .nickName(finalName) // 닉네임은 그냥 name 그대로
                        .profileImage(finalProfileImage)
                        .provider(finalRegistrationId)
                        .role(UserRole.USER) // 기본 USER 권한
                        .password("SOCIAL_LOGIN") // 소셜 로그인은 비번이 없으니 임시 저장
                        .build()
                ));

        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("provider", registrationId);
        customAttributes.put("name", name);
        customAttributes.put("email", email);
        customAttributes.put("profileImage", profileImage);

        Collection<OAuth2UserAuthority> authorities = Collections.singleton(new OAuth2UserAuthority(customAttributes));

        return new DefaultOAuth2User(authorities, customAttributes, "email");
    }

}
