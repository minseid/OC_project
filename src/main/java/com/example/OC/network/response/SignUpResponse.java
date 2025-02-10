package com.example.OC.network.response;

import lombok.Builder;

@Builder
public class SignUpResponse {
    private String userId;
    private String email;
    private String name;
    private String profileImage;
}
