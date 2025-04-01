package com.where.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name="Link")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable=false,unique = true)
    private Place place;

    @Column
    private String naverLink;

    @Column
    private String kakaoLink;
}