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
        // 부모 클래스(DefaultOAuth2UserService)를 사용해 기본 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 클라이언트 등록 ID 확인 (예: kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 정보 키 속성 이름 (카카오는 "id")
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 사용자 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오의 사용자 정보는 "kakao_account"라는 키에 포함되어 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");

        // 필요한 사용자 정보를 가공하거나 추가 처리 가능

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"), // 권한 설정
                attributes, // 사용자 속성
                userNameAttributeName); // 기본 식별자 (id)
    }
}
