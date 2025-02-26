package com.example.OC.network.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String email;
    private String name;
    private String nickname;  // 사용자 닉네임
    private String profileImage;  // 사용자 프로필 이미지 URL
    private boolean existsByNickname;

}
