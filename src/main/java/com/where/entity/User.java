package com.where.entity;

import com.where.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

    private String fcmToken;

    //sns회원가입을 위해서 null허용
    @Column(name = "nick_name") // 'nick_name' 컬럼과 매핑 (스네이크 케이스)
    private String nickName;

    @Column(name = "email", nullable = false, unique = true) // 'email' 컬럼과 매핑
    private String email;

    @Column(name = "password", nullable = false) // 'password' 컬럼과 매핑
    private String password;

    @Column
    private String provider; // "naver", "kakao", "apple" 등 소셜 로그인 제공자

    @Column
    private String providerId; // 소셜 로그인 제공자의 고유 ID


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false) // 'role' 컬럼과 매핑
    private UserRole role;

    @CreatedDate
    @Column(name = "created_at", updatable = false) // 'created_at' 컬럼과 매핑 (스네이크 케이스)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at") // 'updated_at' 컬럼과 매핑 (스네이크 케이스)
    private LocalDateTime updatedAt;

    @Column
    private String appleAccessToken;

    @Column(columnDefinition = "TEXT")
    private String appleRefreshToken;
}
