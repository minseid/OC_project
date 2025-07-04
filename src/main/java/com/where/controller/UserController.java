package com.where.controller;

import com.where.constant.EmailVerify;
import com.where.network.request.CheckEmailRequest;
import com.where.network.request.EditNickNameRequest;
import com.where.network.request.SignUpRequest;
import com.where.network.request.UpdateFcmRequest;
import com.where.network.response.CommonProfileImageResponse;
import com.where.network.response.SignUpResponse;
import com.where.network.response.UserResponse;
import com.where.security.mail.MailService;
import com.where.security.oauth2.KakaoOauthService;
import com.where.service.AwsS3Service;
import com.where.service.KakaoService;
import com.where.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final KakaoService kakaoService;
    private final MailService mailService;
    private final KakaoOauthService kakaoOauthService;

    // 이메일 인증 후 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signupWithVerification(
            @Valid @RequestBody SignUpRequest signUpRequest) {

        /* 프론트요청으로 인한 삭제
        이미 이메일코드를 검증하는 api가 존재하므로 삭제요청
        // 이메일 인증 코드 검증
        EmailVerify isVerified = mailService.verifyCode(signUpRequest.getEmail(), verificationCode);

        if (!isVerified.equals(EmailVerify.Verified)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("이메일 인증에 실패했습니다. 인증 코드를 확인해주세요.");
        }
        */
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
    public ResponseEntity<String> checkEmail(@RequestBody CheckEmailRequest request) {
        boolean isEmailExist = userService.isEmailExist(request.getEmail());
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
    public ResponseEntity<CommonProfileImageResponse> uploadProfileImage(@PathVariable Long userId,
                                     @RequestPart("file") MultipartFile multipartFile) {
        return ResponseEntity.ok(userService.setProfileImage(userId, multipartFile));
    }

    /**
     * 기존 프로필 이미지 수정
     */
    @PutMapping("/{userId}/edit")
    public ResponseEntity<CommonProfileImageResponse> editProfileImage(@PathVariable Long userId,
                                                                       @RequestPart("file") MultipartFile multipartFile) {
        return ResponseEntity.ok(userService.editProfileImage(userId, multipartFile));
    }

    //프로필이미지삭제
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long userId) {
        LocalDate date = LocalDate.now();
        date.toString();
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok().build();
    }

    //닉네임변경
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<Void> editNickName(@PathVariable Long userId, @RequestBody EditNickNameRequest request) {
        userService.editNickName(userId, request.getNickName());
        return ResponseEntity.ok().build();
    }


//    @PostMapping("/kakao")
//    public String kakaoLogin(@RequestBody KakaoLoginRequest request) {
//        return kakaoService.kakaoLogin(request);
//    }

    // FCM 토큰 업데이트 엔드포인트
    @PostMapping("/{userId}/fcm-token")
    public ResponseEntity<Void> updateFcmToken(@PathVariable Long userId,
                                               @RequestBody UpdateFcmRequest request ) {
        userService.updateFcmToken(userId, request.getFcmToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/code")
    public ResponseEntity<String> getState(){
        return ResponseEntity.ok(userService.generateState());
    }

    @GetMapping("/naver/login")
    public ResponseEntity<String> getNaver(HttpServletRequest request){
        return ResponseEntity.ok(request.getParameter("code"));
    }
}
