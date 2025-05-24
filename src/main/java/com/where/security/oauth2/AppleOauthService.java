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
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
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

    @Value("${my.apple.key-id}")
    private String appleKeyId;

    public AppleUserDto login(String authorizationCode) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded"));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", authorizationCode);
        String secrete = generateSecrete();
        params.add("client_secret", secrete);
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
            String sub = payload.getClaim("sub").toString();
            Object email = payload.getClaim("email");
            Optional<User> user = userRepository.findByEmail(email.toString());
            if(user.isEmpty()) { //회원가입
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
                        .profileImage(null)
                        .build();
            } else { //로그인
                user.get().setProviderId(sub);
                user.get().setAppleAccessToken(response.getBody().getAccessToken());
                user.get().setAppleRefreshToken(response.getBody().getRefreshToken());
                return AppleUserDto.builder()
                        .user(userRepository.save(user.get()))
                        .signUp(user.get().getNickName() == null)
                        .profileImage(user.get().getProfileImage())
                        .build();
            }
        } catch (Exception e) {
            log.error("애플 로그인오류 : " + e.getMessage());
            return null;
        }
    }

    private String generateSecrete() throws IOException{
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
        PrivateKey key = getPrivateKey();
        return Jwts.builder()
                .setHeaderParam("kid", appleKeyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(appleTeamId)
                .setAudience("https://appleid.apple.com")
                .setSubject("com.teamwhere.iOS")
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.ES256, key)
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/AuthKey_Z7UV43K5B4.p8");

        try (Reader pemReader = new InputStreamReader(resource.getInputStream())) { // InputStreamReader 사용
            PEMParser pemParser = new PEMParser(pemReader);
            Object object = pemParser.readObject(); // 객체 타입 확인 필요 (PrivateKeyInfo 또는 PEMKeyPair)

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKey privateKey = null;

            if (object instanceof PrivateKeyInfo) {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) object);
            } else if (object instanceof PEMKeyPair) {
                privateKey = converter.getKeyPair((PEMKeyPair) object).getPrivate();
            } else {
                throw new IOException("Unsupported PEM object type or invalid .p8 file content: " + (object != null ? object.getClass().getName() : "null"));
            }

            if (privateKey == null) {
                throw new IOException("Failed to extract private key from .p8 file (privateKey is null).");
            }
            return privateKey;

        } catch (IOException e) {
            // 이 예외는 파일 읽기/파싱 문제일 가능성이 큽니다.
            throw new IOException("Error reading or parsing .p8 private key file: " + e.getMessage(), e);
        } catch (Exception e) { // Bouncy Castle 관련 다른 예외 처리
            throw new IOException("Error converting PEM object to PrivateKey: " + e.getMessage(), e);
        }
    }

}
