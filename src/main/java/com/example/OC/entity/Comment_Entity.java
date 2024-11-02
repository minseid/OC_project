package com.example.OC.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Table(name="comment")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment_Entity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private long meeting_id;

    @Column
    private long user_id;

    @Column
    private Timestamp create_at;

    @Column
    private Timestamp update_at;

    @Column
    private String comment;

}
