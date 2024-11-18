package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Table(name="User")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String User_name;

    private String User_nickname;

    @Column
    private String User_email;

    @Column
    private String User_img;

    @Column
    @CreatedDate // 자동으로 생성시간을 넣어줌
    private LocalDateTime User_created_at;

    @LastModifiedDate // 자동으로 수정시간을 넣어줌
    private LocalDateTime User_update_at;

    @Column
    private long User_token;

    @Column
    private boolean User_alarm;

}
