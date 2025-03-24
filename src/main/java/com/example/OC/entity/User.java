package com.example.OC.entity;

import com.example.OC.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Table(name = "User") // 테이블 이름과 매핑
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false) // 'id' 컬럼과 매핑
    private Long id;

    @Column(name = "name") // 'name' 컬럼과 매핑
    private String name;

    @Column(name = "profile_image") // 'profile_image' 컬럼과 매핑
    private String profileImage;

    @Column(name = "fcm_key") // 'fcm_key' 컬럼과 매핑
    private String fcmKey;

    @Column(name = "nick_name", nullable = false) // 'nick_name' 컬럼과 매핑 (스네이크 케이스)
    private String nickName;

    @Column(name = "email", nullable = false, unique = true) // 'email' 컬럼과 매핑
    private String email;

    @Column(name = "password", nullable = false) // 'password' 컬럼과 매핑
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false) // 'role' 컬럼과 매핑
    private UserRole role;

    @Column(name = "created_at", updatable = false) // 'created_at' 컬럼과 매핑 (스네이크 케이스)
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // 'updated_at' 컬럼과 매핑 (스네이크 케이스)
    private LocalDateTime updatedAt;
}
