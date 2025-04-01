package com.where.entity;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "Meeting")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Meeting extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(unique = true)
    private String link;

    @Column
    private String image;

    @Column(nullable = false)
    private boolean finished;
}
