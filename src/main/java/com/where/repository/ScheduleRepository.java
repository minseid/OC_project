package com.where.repository;

import com.where.entity.Meeting;
import com.where.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByMeeting(Meeting meeting);

    boolean existsByMeeting(Meeting meeting);

    void deleteByMeeting(Meeting meeting);
}
