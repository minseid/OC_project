package com.where.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;
    private Properties properties;

    @Getter @Setter @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter @Setter @NoArgsConstructor
        public static class Profile {
            private String nickname;
            private String profile_image_url;
        }
    }

    @Getter @Setter @NoArgsConstructor
    public static class Properties {
        private String nickname;
        private String profile_image;
    }

    // 편의 메서드
    public String getEmail() {
        return kakao_account != null ? kakao_account.email : null;
    }

    public String getNickname() {
        if (properties != null) return properties.nickname;
        return kakao_account != null && kakao_account.profile != null ?
                kakao_account.profile.nickname : null;
    }

    public String getProfileImageUrl() {
        if (properties != null) return properties.profile_image;
        return kakao_account != null && kakao_account.profile != null ?
                kakao_account.profile.profile_image_url : null;
    }
}