package com.where.repository;

import com.where.entity.Meeting;
import com.where.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMeeting(Meeting meeting);

    void deleteAllByMeeting(Meeting meeting);

    boolean existsByMeetingIdAndFromIdAndToId(Long meetingId, Long fromId, Long toId);
}
