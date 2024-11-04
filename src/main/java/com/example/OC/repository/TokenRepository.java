package com.example.OC.repository;

import com.example.OC.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    List<TokenEntity> findByToken_naver(String token_naver);
    List<TokenEntity> findByToken_kakao(String token_kakao);

}
