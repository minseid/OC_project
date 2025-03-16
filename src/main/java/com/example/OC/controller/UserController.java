package com.example.OC.controller;

import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.network.response.UserResponse;
import com.example.OC.service.AwsS3Service;
import com.example.OC.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // 유저 닉네임 중복확인
    @GetMapping("/checkNickname")
    public ResponseEntity<UserResponse> checkNickname(@RequestParam String nickName) {
        boolean existsByNickName = userService.existsByNickName(nickName);
        UserResponse userResponse = UserResponse.builder()
                .existsByNickName(existsByNickName)
                .build();
        return ResponseEntity.ok(userResponse);
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
    @PostMapping("/{userID}/uploadProfile")
    public ResponseEntity<String> uploadProfile(@PathVariable String userID, @RequestParam("file") MultipartFile file) {
        try{
            String imageUrl = awsS3Service.uploadProfileImage(Long.valueOf(userID),file);
            return ResponseEntity.ok(imageUrl);
        }catch (IOException e){
            return ResponseEntity.badRequest().body("프로필 업로드 실패" + e.getMessage());
        }
    }
}
