package com.where.entity;


import com.where.constant.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="place")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Place extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Meeting meeting;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private float x;

    @Column(nullable = false)
    private float y;

    @ElementCollection
    private List<Long> likes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceStatus placeStatus;
}
