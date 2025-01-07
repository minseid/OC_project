package com.example.OC.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    //공지는0, FAQ는1
    @Column(nullable = false)
    private boolean type;
}
