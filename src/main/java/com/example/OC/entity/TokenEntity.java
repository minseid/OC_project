package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name="Token")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TokenEntity {

    @Id
    @GeneratedValue
    private Long Token_key;

    @Column
    private boolean Token_filled;

    @Column
    private String Token_naver;

    @Column
    private String Token_kakao;

}
