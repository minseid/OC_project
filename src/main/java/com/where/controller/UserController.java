package com.where.controller;

import com.where.entity.User;
import com.where.network.request.KakaoLoginRequest;
import com.where.network.request.SignUpRequest;
import com.where.network.response.SignUpResponse;
import com.where.network.response.UserResponse;
import com.where.security.mail.MailService;
import com.where.service.AwsS3Service;
import com.where.service.KakaoService;
import com.where.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final KakaoService kakaoService;
    private final MailService mailService;

    // 이메일 인증 후 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signupWithVerification(
            @Valid @RequestBody SignUpRequest signUpRequest,
            @RequestParam String verificationCode) {

        // 이메일 인증 코드 검증
        boolean isVerified = mailService.verifyCode(signUpRequest.getEmail(), verificationCode);

        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이메일 인증에 실패했습니다. 인증 코드를 확인해주세요.");
        }

        // 기존 회원가입 로직 수행
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
        UserResponse userResponse = UserResponse.builder()
                .id(signUpResponse.getId())
                .email(signUpResponse.getEmail())
                .nickName(signUpResponse.getNickName())
                .build();

        return ResponseEntity.ok(userResponse);
    }



    // 유저 이메일 중복 확인
    @PostMapping("/checkEmail")
    public ResponseEntity<String> checkEmail(@RequestBody String email) {
        boolean isEmailExist = userService.isEmailExist(email);
        if (isEmailExist) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용중인 이메일입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
    // 유저 마이페이지 조회
    @GetMapping("/mypage/{userId}")
    public ResponseEntity<UserResponse> mypage(@PathVariable String userId) {
        UserResponse userResponse = userService.mypage(userId);
        return ResponseEntity.ok(userResponse);
    }

    // 유저 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> withdraw(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
    /**
     * 새 프로필 이미지 업로드
     */
    @PostMapping("/{userId}/upload")
    public String uploadProfileImage(@PathVariable Long userId,
                                     @RequestPart("file") MultipartFile multipartFile) {
        return awsS3Service.saveProfileImage(multipartFile, userId);
    }

    /**
     * 기존 프로필 이미지 수정
     */
    @PutMapping("/{userId}/edit")
    public String editProfileImage(@PathVariable Long userId,
                                   @RequestPart("file") MultipartFile multipartFile,
                                   @RequestParam("currentImageLink") String currentImageLink) {
        return awsS3Service.editProfileImage(multipartFile, userId, currentImageLink);
    }

    @PostMapping("/kakao")
    public String kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return kakaoService.kakaoLogin(request);
    }
}
