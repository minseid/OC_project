package com.example.OC.controller;

import com.example.OC.network.request.SignUpRequest;
import com.example.OC.network.response.SignUpResponse;
import com.example.OC.network.response.UserResponse;
import com.example.OC.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
        UserResponse userResponse = UserResponse.builder()
                .email(signUpResponse.getEmail())
                .name(signUpResponse.getName())
                .nickname(signUpResponse.getNickname()) // Nickname 추가
                .build();
        return ResponseEntity.ok(userResponse);
    }

    // 유저 닉네임 중복확인
    @GetMapping("/checkNickname")
    public ResponseEntity<UserResponse> checkNickname(@RequestParam String nickname) {
        boolean existsByNickname = userService.existsByNickname(nickname);
        UserResponse userResponse = UserResponse.builder()
                .existsByNickname(existsByNickname)
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
}
