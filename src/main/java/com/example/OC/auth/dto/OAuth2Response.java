package com.example.side.auth.dto;

public interface OAuth2Response {

    /**
     * 제공자 (Ex. naver, google, kakao)
     */
    String getProvider();

    /**
     * 제공자에서 발급해주는 ID(번호)
     */
    String getProviderId();

    /**
     * 이메일
     */
    String getEmail();

    /**
     * 사용자 실명
     */
    String getName();
}
