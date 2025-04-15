package com.where.security.config;

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

        // Check for minimum length
        if (password.length() < MIN_LENGTH) {
            validationErrors.add("Password must be at least " + MIN_LENGTH + " characters long");
        } else {
            score++;
        }

        // Check for maximum length
        if (password.length() > MAX_LENGTH) {
            validationErrors.add("Password must be less than " + MAX_LENGTH + " characters long");
        }

        // Check for uppercase letters
        if (!HAS_UPPER.matcher(password).find()) {
            validationErrors.add("Password must contain at least one uppercase letter");
        } else {
            score++;
        }

        // Check for lowercase letters
        if (!HAS_LOWER.matcher(password).find()) {
            validationErrors.add("Password must contain at least one lowercase letter");
        } else {
            score++;
        }

        // Check for numbers
        if (!HAS_NUMBER.matcher(password).find()) {
            validationErrors.add("Password must contain at least one number");
        } else {
            score++;
        }

        // Check for special characters
        if (!HAS_SPECIAL.matcher(password).find()) {
            validationErrors.add("Password must contain at least one special character");
        } else {
            score++;
        }

        // Calculate strength based on score
        PasswordStrength strength;
        if (score <= 2) {
            strength = new PasswordStrength("weak", validationErrors);
        } else if (score <= 4) {
            strength = new PasswordStrength("medium", validationErrors);
        } else {
            strength = new PasswordStrength("strong", validationErrors);
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