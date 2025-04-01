package com.where.repository;

import com.where.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    boolean existsByLink(String link);

    Optional<Meeting> findByLink(String link);

    //종료된지 3개월이 지난 모임 조회
    List<Meeting> findAllByFinishedIsTrueAndUpdatedAtBefore(LocalDateTime threeMonthsAgo);
}
