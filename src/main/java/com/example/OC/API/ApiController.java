package com.example.OC.API;

import com.example.OC.entity.Token_Entity;
import com.example.OC.entity.User_Entity;
import com.example.OC.repository.*;
import com.example.OC.service.User_service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    User_service userService;

    @GetMapping("/api/user/{navertoken}")
    public User_Entity getuserbynaver(@PathVariable String navertoken ) {
        return userService.find_user_naver(navertoken);
    }

    @GetMapping("/api/user/{kakaotoken}")
    public User_Entity getuserbykakao(@PathVariable String kakaotoken ) {
       return userService.find_user_kakao(kakaotoken);
    }
}
