package com.where.network.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class SignUpResponse {
    private Long id;
    private String nickName;
    private String email;
    private String name;
    private String profileImage;
}
