package com.example.OC.API;

import com.example.OC.entity.UserEntity;
import com.example.OC.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    UserService userService;

    @GetMapping("/api/user/{navertoken}")
    public UserEntity getuserbynaver(@PathVariable String navertoken ) {
        return userService.find_user_naver(navertoken);
    }

    @GetMapping("/api/user/{kakaotoken}")
    public UserEntity getuserbykakao(@PathVariable String kakaotoken ) {
       return userService.find_user_kakao(kakaotoken);
    }
    @PostMapping("/api/meeting")
    public ResponseEntity<MeetingResponse> createMeeting(@AuthenticationPrincipal CustomUserDetails userDetails,MeetingRequest meetingRequest)
    {
        MeetingResponse response = meetingService.createMeeting(userDetails, meetingRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
