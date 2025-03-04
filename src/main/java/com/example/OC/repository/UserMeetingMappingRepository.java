package com.example.OC.repository;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.User;
import com.example.OC.entity.UserMeetingMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMeetingMappingRepository extends JpaRepository<UserMeetingMapping, Long> {
    Optional<UserMeetingMapping> findByUserAndMeeting(User user, Meeting meeting);
}
