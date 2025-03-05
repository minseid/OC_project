package com.example.OC.security.oauth2;

import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService; // DefaultOAuth2UserService로 수정
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@Primary
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // 변경된 부모 클래스
    private final UserRepository userRepository;

    /*
     * OAuth2UserRequest userRequest -> 리소스 서버에서 제공되는 유저정보
     * reqistrationId -> kakao naver apple 식별
     * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User : {}", oAuth2User);
        return processOAuth2User(userRequest, oAuth2User); // OAuth2User 처리
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // 사용자 정보 처리 로직 (예: 사용자 정보 저장/업데이트)
        // 이 부분에서 필요한 사용자 처리 로직을 추가
        return oAuth2User;
    }
}
