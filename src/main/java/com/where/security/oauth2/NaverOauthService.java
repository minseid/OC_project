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
public class NaverOauthService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 부모 클래스(DefaultOAuth2UserService)를 통해 기본 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 클라이언트 등록 ID 확인 (예: "naver")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 네이버의 사용자 정보는 "response"라는 키에 포함되어 있음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        // 사용자 정보 추출
        String id = (String) response.get("id"); // 네이버 고유 ID
        String email = (String) response.get("email"); // 이메일
        String name = (String) response.get("name"); // 이름
        String profileImage = (String) response.get("profile_image"); // 프로필 이미지

        // 필요한 추가 로직 (예: 사용자 데이터베이스 저장 등)을 여기에 구현 가능

        // DefaultOAuth2User를 반환하여 Spring Security에서 인증된 사용자로 처리
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"), // 권한 설정
                response, // 사용자 속성
                "id" // 기본 식별자 (네이버는 "id" 사용)
        );
    }
}
