package com.example.OC.controller;

import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.network.response.UserResponse;
import com.example.OC.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserControlller {
    private final UserService userService;
    //유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(SignUpRequest signUpRequest){ {
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
    }
    //유저 닉네임 중복확인
    @PostMapping("/checkNickname")
    public ResponseEntity<UserResponse> checkNickname(String nickname){
        boolean existsByNickname = userService.existsByNickname(nickname);
        return ResponseEntity.ok(UserResponse.builder().existsByNickname(existsByNickname).build());
    }
    // 유저 마이페이지 조회
    @PostMapping("/mypage")
    public ResponseEntity<UserResponse> mypage(String userId){
        UserResponse userResponse = userService.mypage(userId);
        return ResponseEntity.ok(userResponse);
    }
}
