package com.example.OC.security.oauth2;

import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultReactiveOAuth2UserService {
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
        return processOAuth2User(userRequest, oAuth2User);
    }

}
