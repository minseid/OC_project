package com.where.security.oauth2;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.where.constant.UserRole;
import com.where.entity.User;
import com.where.repository.UserRepository;
import com.where.service.UserService;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.PrivateKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppleOauthService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${my.apple.team-id}")
    private String appleTeamId;

    public AppleUserDto login(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded"));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", authorizationCode);
        params.add("client_secrete", generateSecrete());
        params.add("client_id", "com.teamwhere.iOS");
        HttpEntity<MultiValueMap<String, String>> getAppleUserEntity = new HttpEntity<>(params, headers);
        UriComponentsBuilder getAppleUserBuilder = UriComponentsBuilder.fromHttpUrl("https://appleid.apple.com/auth/token");
        try {
            ResponseEntity<AppleTokenResponseDto> response = restTemplate.exchange(
                    getAppleUserBuilder.toUriString(),
                    HttpMethod.POST,
                    getAppleUserEntity,
                    AppleTokenResponseDto.class
            );
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(response.getBody().getIdToken()));
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();
            log.warn("apple payload : " + payload);
            String sub = payload.getClaim("sub").toString();
            Object email = payload.getClaim("email");
            log.warn("apple CI : " + sub);
            Optional<User> user = userRepository.findByProviderId(sub);
            if(user.isEmpty()) { //회원가입
                log.warn("apple email : " + email);
                return AppleUserDto.builder()
                        .user(userRepository.save(User.builder()
                                .email(email.toString())
                                .password(passwordEncoder.encode("SOCIAL_LOGIN_" + UUID.randomUUID()))
                                .provider("apple")
                                .providerId(sub)
                                .role(UserRole.USER)
                                .appleAccessToken(response.getBody().getAccessToken())
                                .appleRefreshToken(response.getBody().getRefreshToken())
                                .build()))
                        .signUp(true)
                        .build();
            } else { //로그인
                return AppleUserDto.builder()
                        .user(user.get())
                        .signUp(user.get().getNickName() == null)
                        .build();
            }
        } catch (Exception e) {
            log.error("애플 로그인오류 : " + e.getMessage());
            return null;
        }
    }

    private String generateSecrete() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        return Jwts.builder()
                .setHeaderParam("kid", "keyID필요함")
                .setIssuer(appleTeamId)
                .setAudience("https://appleid.apple.com")
                .setSubject("com.teamwhere.iOS")
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setIssuedAt(new Date())
                .signWith(getPrivateKey(), Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        return null;
    }

}
