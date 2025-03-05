package com.example.OC.entity;


import com.example.OC.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Table(name="User")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String img;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    private UserRole role;

    @Column
    private String snsVerified;

    @Column(nullable = false)
    private boolean alarm;

    @Column(nullable = false,unique = true)
    private String fcmKey;


}
