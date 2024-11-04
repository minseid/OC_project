package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Table(name="Place")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PlaceEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private long meeting_id;

    @Column
    private long create_user;

    @Column
    private String place_name;

    @Column
    private String address;

    @Column
    private Timestamp create_at;

    @Column
    private Timestamp update_at;

    @Column
    private long like_count;
}
