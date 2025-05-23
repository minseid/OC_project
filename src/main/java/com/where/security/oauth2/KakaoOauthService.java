package com.where.security.oauth2;

import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOauthService extends DefaultOAuth2UserService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientKey;

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

    public KaKaoLoginDto login(String accessToken, String refreshToken) {
        if(!checkAccessToken(accessToken)) {
            List<String> tokens= getAccessToken(refreshToken);
            if(tokens == null || tokens.isEmpty()) {
                return null;
            }
            accessToken = tokens.get(0);
            if(tokens.get(1) != null) {
                refreshToken = tokens.get(1);
            }
        }
        Optional<User> user = userRepository.findByEmail(getEmail(accessToken));
        if (user.isEmpty()) {
            if(getProfileImageAgree(accessToken)) {
                List<String> userDetail = getEmailAndProfileImage(accessToken);
                return KaKaoLoginDto.builder()
                        .signUp(true)
                        .user(userRepository.save(User.builder()
                        .email(userDetail.get(0))
                        .provider("kakao")
                        .profileImage(userDetail.get(1))
                        .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID())) //sns회원가입은 비밀번호 랜덤
                        .role(UserRole.USER)
                        .build()))
                        .profileImage(userDetail.get(1))
                        .build();
            } else {
                String userEmail = getEmail(accessToken);
                return KaKaoLoginDto.builder()
                        .signUp(true)
                        .user(userRepository.save(User.builder()
                                .email(userEmail)
                                .provider("kakao")
                                .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID())) //sns회원가입은 비밀번호 랜덤
                                .role(UserRole.USER)
                                .build()))
                        .profileImage(null)
                        .build();
            }
        } else {
            return KaKaoLoginDto.builder()
                    .signUp(user.get().getNickName()==null? true: false)
                    .user(user.get())
                    .profileImage(user.get().getProfileImage())
                    .build();
        }
    }

    public boolean checkAccessToken(String accessToken) {
        HttpHeaders checkTokenHeaders = new HttpHeaders();
        checkTokenHeaders.setBearerAuth(accessToken);

        HttpEntity<String> checkTokenEntity = new HttpEntity<>(checkTokenHeaders);
        UriComponentsBuilder checkTokenUrlBuilder = UriComponentsBuilder.fromHttpUrl("https://kapi.kakao.com/v1/user/access_token_info");


        String checkTokenUrl = checkTokenUrlBuilder.toUriString();
        try {
            ResponseEntity<KakaoCheckAccessTokenDto> checkTokenResponse = restTemplate.exchange(
                    checkTokenUrl,
                    HttpMethod.GET,
                    checkTokenEntity,
                    KakaoCheckAccessTokenDto.class
            );
            return(checkTokenResponse.getStatusCode() == HttpStatus.OK && checkTokenResponse.getBody() != null && checkTokenResponse.getBody().getExpires_in()>60);
        } catch (HttpClientErrorException e) { //4XX 에러
            if(e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                log.error("카카오 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            }
            return false;
        } catch (HttpServerErrorException e) {
            log.error("카카오 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.error("카카오 토큰검증오류 : " + e.getMessage());
            return false;
        }
    }

    public List<String> getAccessToken(String refreshToken) {

        HttpHeaders getAccessTokenHeaders = new HttpHeaders();
        getAccessTokenHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", kakaoClientKey);
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> getAccessTokenEntity = new HttpEntity<>(params, getAccessTokenHeaders);

        UriComponentsBuilder getAccessTokenUrlBuilder = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token");
        String getAccessTokenUrl = getAccessTokenUrlBuilder.toUriString();
        try{
            ResponseEntity<GetKakaoAccessTokenResponse> checkTokenResponse = restTemplate.exchange(
                    getAccessTokenUrl,
                    HttpMethod.POST,
                    getAccessTokenEntity,
                    GetKakaoAccessTokenResponse.class
            );
            if(checkTokenResponse.getStatusCode() == HttpStatus.OK && checkTokenResponse.getBody() != null && checkTokenResponse.getBody().getAccess_token()!=null) {
                return Arrays.asList(checkTokenResponse.getBody().getAccess_token(), checkTokenResponse.getBody().getRefresh_token());
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) { //4XX 에러
            if(e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                log.error("카카오 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            }
            return null;
        } catch (HttpServerErrorException e) {
            log.error("카카오 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("카카오 토큰검증오류 : " + e.getMessage());
            return null;
        }
    }

    public boolean getProfileImageAgree(String accessToken) {

        HttpHeaders getAgreeHeaders = new HttpHeaders();
        getAgreeHeaders.setBearerAuth(accessToken);
        HttpEntity<String> getAgreeEntity = new HttpEntity<>(getAgreeHeaders);
        ResponseEntity<KakaoAgreeDto> getAgreeResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/scopes?scopes=[\"profile_image\"]",
                HttpMethod.GET,
                getAgreeEntity,
                KakaoAgreeDto.class
        );
        return getAgreeResponse.getBody().getScopes().get(0).isAgreed();

    }

    public String getEmail(String accessToken) {
        HttpHeaders getEmailHeaders = new HttpHeaders();
        getEmailHeaders.setBearerAuth(accessToken);
        getEmailHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
        HttpEntity<String> getEmailEntity = new HttpEntity<>(getEmailHeaders);
        ResponseEntity<KakaoUserDto> getAgreeResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me?property_keys=[\"kakao_account.email\",\"kakao_account.profile\"]",
                HttpMethod.POST,
                getEmailEntity,
                KakaoUserDto.class
        );
        return getAgreeResponse.getBody().getKakao_account().getEmail();
    }

    public List<String> getEmailAndProfileImage(String accessToken) {
        HttpHeaders getEmailHeaders = new HttpHeaders();
        getEmailHeaders.setBearerAuth(accessToken);
        getEmailHeaders.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=utf-8"));
        HttpEntity<String> getEmailEntity = new HttpEntity<>(getEmailHeaders);
        ResponseEntity<KakaoUserDto> getAgreeResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me?property_keys=[\"kakao_account.email\",\"kakao_account.profile\"]",
                HttpMethod.POST,
                getEmailEntity,
                KakaoUserDto.class
        );
        return Arrays.asList(getAgreeResponse.getBody().getKakao_account().getEmail(),getAgreeResponse.getBody().getKakao_account().getProfile().getProfile_image_url());
    }
}
