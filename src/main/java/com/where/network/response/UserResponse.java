package com.where.network.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickName;  // 사용자 닉네임
    private String profileImage;  // 사용자 프로필 이미지 URL
    private boolean existsByNickName;
}
