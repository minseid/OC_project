package com.example.OC.repository;

import com.example.OC.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    boolean existsByLink(String link);

    //종료된지 3개월이 지난 모임 조회
    List<Meeting> findAllByFinishedIsTrueAndUpdatedAtBefore(LocalDateTime threeMonthsAgo);
}
