package com.example.OC.repository;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMeeting(Meeting meeting);
}
