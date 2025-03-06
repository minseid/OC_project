package com.example.OC.security.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.spring31.properties.EncryptablePropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    private static final String SECRET_KEY = "your-secret-key"; // 암호화/복호화에 사용할 비밀 키

    @Bean
    public PooledPBEStringEncryptor encryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        PBEConfig config = new PBEConfig();
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 알고리즘 설정
        config.setPassword(SECRET_KEY); // 비밀 키 설정

        encryptor.setConfig(config);
        return encryptor;
    }

    @Bean
    public EncryptablePropertyResolver encryptablePropertyResolver(PooledPBEStringEncryptor encryptor) {
        return new EncryptablePropertyResolver(encryptor);
    }
}
