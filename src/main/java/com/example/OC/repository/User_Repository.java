package com.example.OC.repository;

import com.example.OC.entity.User_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_Repository extends JpaRepository<User_Entity, Long> {

    List<User_Entity> findByUser_token(Long user_token);
}
