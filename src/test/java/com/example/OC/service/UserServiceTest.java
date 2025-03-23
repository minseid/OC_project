package com.example.OC.service;

import com.example.OC.network.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void signUp() {
        System.out.println("================ 테스트1===================");
        System.out.println(userService.signUp(SignUpRequest.builder()
                        .email("ppk052@naver.com")
                        .password("dream61398")
                        .nickName("테스트1")
                        .build()).toString());
        System.out.println("================ 테스트2,중복이메일===================");
        System.out.println(userService.signUp(SignUpRequest.builder()
                .email("ppk052@naver.com")
                .password("dream61398@")
                .nickName("테스트2")
                .build()).toString());
        System.out.println("================ 테스트3,닉네임반복===================");
        System.out.println(userService.signUp(SignUpRequest.builder()
                .email("ppk058@naver.com")
                .password("dream6@$1398")
                .nickName("테스트1")
                .build()).toString());
    }

    @Test
    void isEmailExist() {
    }

    @Test
    void mypage() {
    }

    @Test
    void verifyPassword() {
    }

    @Test
    void findUserByEmail() {
    }

    @Test
    void deleteUser() {
    }
}