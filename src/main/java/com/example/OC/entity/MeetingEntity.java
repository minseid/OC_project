package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Table(name="Social")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MeetingEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column
    private String link;

    @Column
    private Timestamp create_at;
}
