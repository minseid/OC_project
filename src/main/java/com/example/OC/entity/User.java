package com.example.OC.entity;

import com.example.OC.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략
    @Column // 'User_id' 컬럼과 매핑
    private Long id;

    @Column(name = "User_name") // 'User_name' 컬럼과 매핑
    private String name;

    @Column(name = "User_image") // 'User_image' 컬럼과 매핑
    private String profileImage;

    @Column // 'fcmKey' 컬럼과 매핑 (테이블에 없으면 삭제)
    private String fcmKey;

    @Column(nullable = false, name = "User_nickname") // 'User_nickname' 컬럼과 매핑
    private String nickName;

    @Column(name = "User_email") // 'User_email' 컬럼과 매핑
    private String email;

    @Column(name = "User_password") // 'User_password' 컬럼과 매핑
    private String password;

    @CreatedDate // 생성 시간 자동 관리 (Auditing 활성화 필요)
    @Column(name = "User_created_at", updatable = false) // 'User_created_at' 컬럼과 매핑
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    private UserRole role; // 사용자 역할

    @Column(name = "sms_verified") // 'sns_verified' 컬럼과 매핑 (테이블에 없으면 삭제)
    private String snsVerified;

    @Column(nullable = false, name = "User_alarm") // 'User_alarm' 컬럼과 매핑
    private boolean alarm;
}
