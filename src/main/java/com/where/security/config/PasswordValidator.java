package com.where.security.validation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public PasswordStrength validatePassword(String password) {
        List<String> validationErrors = new ArrayList<>();
        int score = 0;

        // 최소 길이 확인
        if (password.length() < MIN_LENGTH) {
            validationErrors.add("비밀번호는 최소 " + MIN_LENGTH + "자 이상이어야 합니다");
        } else {
            score++;
        }

        // 최대 길이 확인
        if (password.length() > MAX_LENGTH) {
            validationErrors.add("비밀번호는 " + MAX_LENGTH + "자 미만이어야 합니다");
        }

        // 대문자 확인
        if (!HAS_UPPER.matcher(password).find()) {
            validationErrors.add("비밀번호에는 최소한 하나의 대문자가 포함되어야 합니다");
        } else {
            score++;
        }

        // 소문자 확인
        if (!HAS_LOWER.matcher(password).find()) {
            validationErrors.add("비밀번호에는 최소한 하나의 소문자가 포함되어야 합니다");
        } else {
            score++;
        }

        // 숫자 확인
        if (!HAS_NUMBER.matcher(password).find()) {
            validationErrors.add("비밀번호에는 최소한 하나의 숫자가 포함되어야 합니다");
        } else {
            score++;
        }

        // 특수 문자 확인
        if (!HAS_SPECIAL.matcher(password).find()) {
            validationErrors.add("비밀번호에는 최소한 하나의 특수 문자가 포함되어야 합니다");
        } else {
            score++;
        }

        // 점수 기반으로 강도 계산
        PasswordStrength strength;
        if (score <= 2) {
            strength = new PasswordStrength("약함", validationErrors);
        } else if (score <= 4) {
            strength = new PasswordStrength("중간", validationErrors);
        } else {
            strength = new PasswordStrength("강함", validationErrors);
        }

        return strength;
    }

    public static class PasswordStrength {
        private final String strength;
        private final List<String> validationErrors;

        public PasswordStrength(String strength, List<String> validationErrors) {
            this.strength = strength;
            this.validationErrors = validationErrors;
        }

        public String getStrength() {
            return strength;
        }

        public List<String> getValidationErrors() {
            return validationErrors;
        }

        public boolean isValid() {
            return validationErrors.isEmpty();
        }
    }
}