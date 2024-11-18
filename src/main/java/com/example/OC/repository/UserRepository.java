package com.example.OC.repository;

import com.example.OC.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUser_token(Long user_token);
}
