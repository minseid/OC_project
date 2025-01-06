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
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable=false)
    private Place place;

    @Column
    private String naverLink;

    @Column
    private String kakaoLink;
}
