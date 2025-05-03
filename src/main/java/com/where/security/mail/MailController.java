package com.where.security.mail;

import com.where.constant.EmailVerify;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
class MailController {
    private final MailService mailService;


    // 인증 코드 요청 엔드포인트
    @GetMapping("/auth/{email}")
    public ResponseEntity<String> requestAuthCode(@PathVariable String email) {
        try {
            mailService.sendSimpleMessage(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("인증 코드 발급에 실패하였습니다: " + e.getMessage());
        }
    }

    // 인증 코드 검증 엔드포인트
    @PostMapping("/verify")
    public ResponseEntity<EmailVerify> verifyCode(@RequestBody VerificationRequest request) {
        EmailVerify isValid = mailService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(isValid);
    }

    // 인증 요청 응답 DTO
    static class VerificationRequest {
        private String email;
        private String code;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
