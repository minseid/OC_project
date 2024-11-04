package com.example.OC.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name="Link")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LinkEntity {

    @Id
    @GeneratedValue
    private Long place_id;

    @Column
    private String naver_link;

    @Column
    private String kakao_link;

    @Column
    private String google_link;
}
