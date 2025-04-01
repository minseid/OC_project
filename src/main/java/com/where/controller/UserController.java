package com.where.controller;

import com.where.network.request.SignUpRequest;
import com.where.network.response.SignUpResponse;
import com.where.network.response.UserResponse;
import com.where.service.AwsS3Service;
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

    // 유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
        UserResponse userResponse = UserResponse.builder()
                .email(signUpResponse.getEmail())
                .nickName(signUpResponse.getNickName()) // Nickname 추가
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
    //유저 프로필 이미지 등록
    @PostMapping("/{userId}/uploadProfile")
    public ResponseEntity<String> uploadProfile(@PathVariable String userID, @RequestParam("file") MultipartFile file) {
        try{
            String imageUrl = awsS3Service.uploadProfileImage(Long.valueOf(userID),file);
            return ResponseEntity.ok(imageUrl);
        }catch (IOException e){
            return ResponseEntity.badRequest().body("프로필 업로드 실패" + e.getMessage());
        }
    }
}
