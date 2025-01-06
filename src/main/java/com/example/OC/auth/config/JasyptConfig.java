package com.example.side.auth.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

//    @Value("${jasypt.encryptor.password}") 외부 환경변수를 어노테이션으로 주입받아도 되고 아래처럼 설정하는 방법도 있다.
//    private String JASTYPTKEY;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // !!주의: 암복호화에 사용되는 키로 1-4번 과정에서 설정하는 변수명이랑 동일해야 한다.
        config.setPassword(System.getenv("JASYPTKEY"));

        config.setAlgorithm("PBEWITHMD5ANDDES"); // 암호화 알고리즘
        config.setPoolSize("1");				 // 암호화 인스턴스 pool
        config.setKeyObtentionIterations("1000"); // 반복할 해싱 횟수
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
        config.setStringOutputType("base64");    // 인코딩 방식

        encryptor.setConfig(config);
        return encryptor;
    }
}
