package com.where.repository;

import com.where.entity.Meeting;
import com.where.entity.User;
import com.where.entity.UserMeetingMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMeetingMappingRepository extends JpaRepository<UserMeetingMapping, Long> {

    Optional<UserMeetingMapping> findByUserAndMeeting(User user, Meeting meeting);

    List<UserMeetingMapping> findByUser(User user);

    List<UserMeetingMapping> findAllByMeeting(Meeting meeting);

    boolean existsByUserAndMeeting(User user, Meeting meeting);

}
