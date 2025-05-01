package com.where.repository;

import com.where.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이미 존재하는지 확인
     * 자체 회원가입 아이디 중복확인
     * @param id
     */
    boolean existsById(Long id);

    /**
     * 닉네임 중복확인
     * @param nickName
     */
    boolean existsByNickName(String nickName);

    /**
     * 이메일 중복확인
     * @param email
     */
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
