package com.example.OC.repository;

import com.example.OC.entity.Token_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Token_Repository extends JpaRepository<Token_Entity, Long> {

    List<Token_Entity> findByToken_naver(String token_naver);
    List<Token_Entity> findByToken_kakao(String token_kakao);

}
