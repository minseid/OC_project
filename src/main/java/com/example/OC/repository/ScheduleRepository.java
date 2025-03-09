package com.example.OC.repository;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByMeeting(Meeting meeting);
}
