package com.example.OC.network.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SignUpResponse {
    private String userId;
    private String nickName;
    private String email;
    private String name;
    private String profileImage;
}
