package com.example.side.auth.dto;

import java.util.Map;

/**
 * 추후 네이버 추가 후 수정
 */
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attributes) {
        this.attribute = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "";
    }

    @Override
    public String getProviderId() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }
}
