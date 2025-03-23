package com.example.OC.network.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class SignUpResponse {
    private String userId;
    private String nickName;
    private String email;
    private String name;
    private String profileImage;
}
