package com.example.OC.repository;

import com.example.OC.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByU1OrU2(Long u1, Long u2);
}
