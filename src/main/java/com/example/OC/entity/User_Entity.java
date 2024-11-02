package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Table(name="User")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User_Entity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String User_name;

    @Column
    private String User_nickname;

    @Column
    private String User_email;

    @Column
    private String User_img;

    @Column
    private Timestamp User_created_at;

    @Column
    private long User_token;

    @Column
    private boolean User_alarm;

}
