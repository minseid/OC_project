package com.example.OC.repository;

import com.example.OC.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByToken_naver(String token_naver);
    List<Token> findByToken_kakao(String token_kakao);

}
