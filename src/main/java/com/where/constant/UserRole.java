package com.where.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "일반사용자");

    private final String key;
    private final String title;

}
