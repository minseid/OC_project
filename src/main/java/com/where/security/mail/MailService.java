package com.where.security.mail;

import com.where.constant.EmailVerify;
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
            // Code expires after 10 minutes
            return System.currentTimeMillis() - timestamp > TimeUnit.MINUTES.toMillis(10);
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
        body += "<!DOCTYPE html>";
        body += "<html>";
        body += "<head>";
        body += "</head>";
        body += "<body style=\"text-align: center;\">";
        body += "  <table style=\"width: 80vw; margin: 0 auto; border-collapse: collapse;\">";
        body += "    <tr>";
        body += "      <td>";
        body += "        <img src=\"https://audiwhere.shop/image/emailTop.png\" alt=\"이미지\" style=\"display: block; width: 100%; height: auto;\">";
        body += "      </td>";
        body += "    </tr>";
        body += "    <tr>";
        body += "      <td style=\"padding: 20px 0; text-align: center; color: black;\">";
        body += "        <div style=\"font-size: calc(3vw + 0.5em); font-weight: 500; margin-bottom: calc(2vw + 0.5em)\"></div>";
        body += "        <div style=\"font-size: calc(1.5vw + 0.5em); font-weight: 500;\">확인코드</div>";
        body += "        <div style=\"font-size: calc(4.5vw + 1em); font-weight: 700; margin-bottom: calc(1vw + 0.2em);\">" + authCode + "</div>";
        body += "        <div style=\"font-size: calc(1vw + 0.3em); margin-bottom: 15vw; color:gray;\">이 코드는 전송 10분 후에 만료됩니다.</div>";
        body += "        <div style=\"font-size: calc(0.7vw + 0.3em);  color:gray; margin-bottom: calc(1vw + 0.2em);\">본 인증 코드는 이메일 인증 용도로만 사용되며, 안전하게 보호됩니다.</div>";
        body += "        <div style=\"font-size: calc(0.7vw + 0.3em);  color:gray;\">타인과 공유하지마세요.</div>";
        body += "      </td>";
        body += "    </tr>";
        body += "  </table>";
        body += "</body>";
        body += "</html>";
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
    public EmailVerify verifyCode(String email, String code) {
        EmailVerification verification = verificationCodes.get(email);

        if (verification == null) {
            return EmailVerify.NotSend; // No verification code for this email
        }

        if (verification.isExpired()) {
            verificationCodes.remove(email); // Clean up expired code
            return EmailVerify.Expired; // Code expired
        }

        boolean isValid = verification.getCode().equals(code);

        if (isValid) {
            verificationCodes.remove(email); // Clean up used code
            return EmailVerify.Verified;
        } else {
            return EmailVerify.NotVerified;
        }


    }
}