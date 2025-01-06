package com.example.side.auth.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testJWT() {

        return "ok";
    }

    @GetMapping("/main")
    public String mainOk() {

        return "main";
    }

}
