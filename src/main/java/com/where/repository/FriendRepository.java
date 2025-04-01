package com.where.repository;

import com.where.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByU1OrU2(Long u1, Long u2);

    @Query("SELECT f FROM Friend f WHERE f.u1 = :userId OR f.u2 = :userId")
    List<Friend> findAllByUser(@Param("userId") Long userId);

    Optional<Friend> findByU1AndU2(Long u1, Long u2);

    @Query("SELECT f From Friend f WHERE (f.u1= :u1 AND f.u2= :u2) OR (f.u1= :u2 AND f.u2= :u1)")
    Optional<Friend> findByUserSet(@Param("u1")Long u1, @Param("u2") Long u2);

    boolean existsByU1AndU2(Long u1, Long u2);

    void deleteByU1AndU2(Long u1, Long u2);
}
