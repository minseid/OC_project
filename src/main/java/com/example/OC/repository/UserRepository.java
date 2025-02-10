package com.example.OC.repository;

import com.example.OC.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이미 존재하는지 확인
     * 자체 회원가입 아이디 중복확인
     * @param uid
     * @return
     */
    boolean existsByUid(String uid);

    /**
     * 닉네임 중복확인
     * @param nickname
     * @return
     */
    boolean existsByNickname(String nickname);

    /**
     * 이메일 중복확인
     * @param email
     * @return
     */
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
