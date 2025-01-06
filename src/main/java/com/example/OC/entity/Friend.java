package com.example.OC.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="Friend")
@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Long u1;

    @Column(nullable = false)
    private Long u2;

    //u1이 u2를 즐겨찾기했는가
    @Column(nullable = false)
    private boolean u1Bookmark;

    //u2가 u1을 즐겨찾기했는가
    @Column(nullable = false)
    private boolean u2Bookmark;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> meets;
}
