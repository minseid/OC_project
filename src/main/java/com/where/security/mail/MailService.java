package com.where.security.mail;

import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Store email verification codes with timestamps (in a real application, use Redis or another cache)
    private final Map<String, EmailVerification> verificationCodes = new HashMap<>();

    // Inner class to store verification code and timestamp
    private static class EmailVerification {
        private final String code;
        private final long timestamp;

        public EmailVerification(String code) {
            this.code = code;
            this.timestamp = System.currentTimeMillis();
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            // Code expires after 5 minutes
            return System.currentTimeMillis() - timestamp > TimeUnit.MINUTES.toMillis(5);
        }
    }

    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) { // 인증 코드 6자리
            int index = random.nextInt(2); // 0~1까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 1 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String authCode = createCode(); // 랜덤 인증번호 생성

        // 인증코드 저장
        verificationCodes.put(sendEmail, new EmailVerification(authCode));

        MimeMessage message = createMail(sendEmail, authCode); // 메일 생성
        try {
            javaMailSender.send(message); // 메일 발송
            return authCode; // For testing purposes, you might want to return the code
        } catch (MailException e) {
            throw new RuntimeException("메일 발송에 실패했습니다: " + e.getMessage());
        }
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = verificationCodes.get(email);

        if (verification == null) {
            return false; // No verification code for this email
        }

        if (verification.isExpired()) {
            verificationCodes.remove(email); // Clean up expired code
            return false; // Code expired
        }

        boolean isValid = verification.getCode().equals(code);

        if (isValid) {
            verificationCodes.remove(email); // Clean up used code
        }

        return isValid;
    }
}