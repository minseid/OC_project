package com.example.side.auth;

import com.example.side.auth.dto.GoogleResponse;
import com.example.side.auth.dto.NaverResponse;
import com.example.side.auth.dto.OAuth2Response;
import com.example.side.user.dto.request.UserOAuth2Dto;
import com.example.side.user.entity.User;
import com.example.side.user.entity.UserRole;
import com.example.side.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2UserRequest userRequest -> 리소스 서버에서 제공되는 유저 정보
     * registrationId -> google, kakao, naver인지 식별
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        /**
         * username -> 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디
         * ex) google_2398472981di
         */
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Optional<User> existData = userRepository.findByUid(username);
        if (!existData.isPresent()) { // 한 번도 로그인하지 않은 경우
            User user = User
                    .builder()
                    .uid(username)
                    .email(oAuth2Response.getEmail())
                    .role(UserRole.USER)
                    .build();

            userRepository.save(user);

            UserOAuth2Dto userOAuth2Dto = new UserOAuth2Dto();
            userOAuth2Dto.setUid(username);
            userOAuth2Dto.setNickname(oAuth2Response.getName());
            userOAuth2Dto.setRole(UserRole.USER.getKey());

            return new CustomOAuth2User(userOAuth2Dto);
        }
        else { // 로그인 했던 이력이 있는 경우

            existData.get().updateUserInfo(oAuth2Response.getEmail(), oAuth2Response.getName());

            UserOAuth2Dto userOAuth2Dto = new UserOAuth2Dto();
            userOAuth2Dto.setUid(existData.get().getUid());
            userOAuth2Dto.setNickname(existData.get().getNickname());
            userOAuth2Dto.setRole(UserRole.USER.getKey());

            return new CustomOAuth2User(userOAuth2Dto);
        }


    }
}