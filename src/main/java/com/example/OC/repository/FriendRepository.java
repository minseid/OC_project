package com.example.OC.repository;

import com.example.OC.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByU1OrU2(Long u1, Long u2);

    @Query("SELECT f FROM Friend f WHERE f.u1 = :userId OR f.u2 = :userId")
    List<Friend> findAllByUser(@Param("userId") Long userId);

    Optional<Friend> findByU1AndU2(Long u1, Long u2);

    boolean existsByU1AndU2(Long u1, Long u2);

    void deleteByU1AndU2(Long u1, Long u2);
}
