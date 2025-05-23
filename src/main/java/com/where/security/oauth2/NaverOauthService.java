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
public class NaverOauthService extends DefaultOAuth2UserService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverSecret;



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

    public NaverLoginDto login(String accessToken, String refreshToken) {
        if(!checkAccessToken(accessToken)) {
            List<String> token = getAccessToken(refreshToken);
            if(token == null || token.isEmpty()) {
                return null;
            }
            accessToken = token.get(0);
        }
        NaverUserDetailDto naverUserDetailDto = getProfile(accessToken);
        Optional<User> user = userRepository.findByEmail(naverUserDetailDto.getEmail());
        if(user.isEmpty()) {
            if(getProfileImageAgree(accessToken)) {
                return NaverLoginDto.builder()
                        .signUp(true)
                        .user(userRepository.save(User.builder()
                                .email(naverUserDetailDto.getEmail())
                                .provider("naver")
                                .profileImage(naverUserDetailDto.getProfile_image())
                                .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID()))
                                .role(UserRole.USER)
                                .build()))
                        .profileImage(naverUserDetailDto.getProfile_image())
                        .build();
            } else {
                return NaverLoginDto.builder()
                        .signUp(true)
                        .user(userRepository.save(User.builder()
                                .email(naverUserDetailDto.getEmail())
                                .provider("naver")
                                .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID()))
                                .role(UserRole.ADMIN)
                                .build()))
                        .profileImage(null)
                        .build();
            }
        } else {
            return NaverLoginDto.builder()
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
        UriComponentsBuilder checkTokenUrlBuilder = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/nid/me");


        String checkTokenUrl = checkTokenUrlBuilder.toUriString();
        try {
            ResponseEntity<NaverCheckAccessTokenDto> checkTokenResponse = restTemplate.exchange(
                    checkTokenUrl,
                    HttpMethod.GET,
                    checkTokenEntity,
                    NaverCheckAccessTokenDto.class
            );
            return(checkTokenResponse.getStatusCode() == HttpStatus.OK && checkTokenResponse.getBody() != null && checkTokenResponse.getBody().getMessage().contains("success"));
        } catch (HttpClientErrorException e) { //4XX 에러
            if(e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                log.error("네이버 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", naverId);
        params.add("client_secret", naverSecret);
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> getAccessTokenEntity = new HttpEntity<>(params, getAccessTokenHeaders);

        UriComponentsBuilder getAccessTokenUrlBuilder = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token");
        String getAccessTokenUrl = getAccessTokenUrlBuilder.toUriString();
        try{
            ResponseEntity<GetNaverAccessTokenResponse> checkTokenResponse = restTemplate.exchange(
                    getAccessTokenUrl,
                    HttpMethod.POST,
                    getAccessTokenEntity,
                    GetNaverAccessTokenResponse.class
            );
            if(checkTokenResponse.getStatusCode() == HttpStatus.OK && checkTokenResponse.getBody() != null && checkTokenResponse.getBody().getAccess_token()!=null) {
                return Arrays.asList(checkTokenResponse.getBody().getAccess_token(), checkTokenResponse.getBody().getRefresh_token());
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) { //4XX 에러
            if(e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                log.error("네이버 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            }
            return null;
        } catch (HttpServerErrorException e) {
            log.error("네이버 토큰검증오류 : " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("카카오 토큰검증오류 : " + e.getMessage());
            return null;
        }
    }

    public boolean getProfileImageAgree(String accessToken) {
        HttpHeaders getAgreeHeaders = new HttpHeaders();
        getAgreeHeaders.setBearerAuth(accessToken);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("info", "true");
        HttpEntity<MultiValueMap<String, String>> getAgreeEntity = new HttpEntity<>(params, getAgreeHeaders);
        ResponseEntity<NaverAgreeDto> getAgreeResponse = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/verify",
                HttpMethod.GET,
                getAgreeEntity,
                NaverAgreeDto.class
        );
        return getAgreeResponse.getBody().getResponse().getAllowed_profile().contains("profile/profileimage");
    }

    public NaverUserDetailDto getProfile(String accessToken) {
        HttpHeaders getEmailHeaders = new HttpHeaders();
        getEmailHeaders.setBearerAuth(accessToken);
        HttpEntity<String> getEmailEntity = new HttpEntity<>(getEmailHeaders);
        ResponseEntity<NaverUserDto> getAgreeResponse = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                getEmailEntity,
                NaverUserDto.class
        );
        log.warn("받은 Dto : " + getAgreeResponse.getBody());
        return getAgreeResponse.getBody().getResponse();
    }
}
