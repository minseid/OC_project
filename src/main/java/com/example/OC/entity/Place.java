package com.example.OC.entity;


import com.example.OC.constant.PlaceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Table(name="Place")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Place extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Meeting meeting;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private List<User> user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private long like_count;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceStatus placeStatus;
}
