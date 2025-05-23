package com.where.security.oauth2;

import com.where.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverLoginDto {

    private User user;

    private boolean signUp;

    private String profileImage;
}
