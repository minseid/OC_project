package com.where.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.where.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppleUserDto {

    private boolean signUp;

    private User user;

    private String profileImage;
}
