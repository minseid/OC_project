package com.where.security.mail;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class MemberController {
    private final MailService mailService;

    public MemberController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/email/auth/{email}")
    public ResponseEntity<String> requestAuthcode(@PathVariable String email) throws MessagingException {
        boolean isSend = mailService.sendSimpleMessage(email);
        return isSend ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 전송되었습니다.") :
                ResponseEntity.status(HttpStatus.OK).body("인증 코드 발급에 실패하였습니다.");
    }
}
