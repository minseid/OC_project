package com.where.entity;


import com.where.constant.PlaceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="Place")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Place extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
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
    @CollectionTable(name = "place_like_users", joinColumns = @JoinColumn(name="place_id"))
    @Column(nullable = false)
    private List<Long> likes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceStatus placeStatus;
}
