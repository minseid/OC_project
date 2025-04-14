package com.where.security.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
@Service
public class KakaoOauthService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // kakao_account 가져오기
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        // email 가져오기
        String email = (String) kakaoAccount.get("email");

        // nickname 가져오기
        String nickname = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");

        // ⭐ profile 정보에서 프로필 이미지 가져오기
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String profileImageUrl = (String) profile.get("profile_image_url");

        // 이제 email, nickname, profileImageUrl 다 가져왔어
        System.out.println("email = " + email);
        System.out.println("nickname = " + nickname);
        System.out.println("profileImageUrl = " + profileImageUrl);

        // attributes에 직접 profileImageUrl 추가하고 싶으면 이렇게
        attributes.put("email", email);
        attributes.put("nickname", nickname);
        attributes.put("profile_image_url", profileImageUrl);

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"), // 권한 설정
                attributes, // 사용자 속성
                userNameAttributeName); // 기본 식별자 (id)
    }
}
