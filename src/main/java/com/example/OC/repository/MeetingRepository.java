package com.example.OC.repository;

import com.example.OC.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByLink(String link);

    boolean existsByLink(String link);
}
