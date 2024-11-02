package com.example.OC.dto;


import com.example.OC.entity.Token_Entity;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Token_dto {

    private Long Token_key;
    private boolean Token_filled;
    private String Token_naver;
    private String Token_kakao;
    public Token_Entity toEntity() {
        return new Token_Entity(Token_key, Token_filled, Token_naver, Token_kakao);
    }
}
