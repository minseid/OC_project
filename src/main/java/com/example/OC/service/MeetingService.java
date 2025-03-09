package com.example.OC.service;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.entity.User;
import com.example.OC.entity.UserMeetingMapping;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.ParticipantRepository;
import com.example.OC.repository.UserMeetingMappingRepository;
import com.example.OC.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Getter
@ToString
@Builder
@Transactional
@Slf4j
public class MeetingService extends FindService {

    private String makeLink(){

        while (true) {
            Random rnd = new Random();
            StringBuilder buf = new StringBuilder();

            for (int i = 0; i < 10; i++) {
                if (rnd.nextBoolean()) {
                    buf.append((char) (rnd.nextInt(26) + 97));
                } else {
                    buf.append(rnd.nextInt(10));
                }
            }

            String randomCode = buf.toString();
            if (meetingRepository.findByLink(randomCode).isEmpty()) {
                return randomCode;
            }
        }
    }

    public Meeting addMeeting(String title, String description, String image, Long fromId, List<Long> friends ) {

        Meeting target = Meeting.builder()
                .title(title)
                .description(description)
                .link(makeLink())
                .image(image)
                .build();
        friends.forEach(id -> participantRepository.save(Participant.builder()
                .meeting(target)
                .fromId(fromId)
                .toId(id)
                .status(false)
                .build()));
        userMeetingMappingRepository.save(UserMeetingMapping.builder()
                .meeting(target)
                .user(findUser(fromId))
                .build());
        //초대관련 넣어야됨
        return meetingRepository.save(target);
    }

    public Meeting editMeeting(Long id, String title, String description, String image) {

        Meeting target = Meeting.builder()
                .id(id)
                .title(title)
                .description(description)
                .link(findMeeting(id).getLink())
                .image(image)
                .build();
        return meetingRepository.save(target);
    }

    public Meeting quitMeeting(Long userId, Long meetingId) {

        userMeetingMappingRepository.delete(findUserMeetingMapping(userId, meetingId));
        return findMeeting(meetingId);
    }

    public List<Participant> loadParticipants(Long meetingId) {

        Meeting targetMeeting = findMeeting(meetingId);
        return participantRepository.findAllByMeeting(targetMeeting);
    }

    public List<Meeting> getMeetings(Long userId) {

        User targetUser = findUser(userId);
        return userMeetingMappingRepository.findByUser(targetUser);
    }

    public Participant addParticipant(Long meetingId, Long fromId, Long toId) {

        findUser(fromId);
        findUser(toId);
        return participantRepository.save(Participant.builder()
                .meeting(findMeeting(meetingId))
                .fromId(fromId)
                .toId(toId)
                .status(false)
                .build());
    }

    public Meeting inviteOk(Long id) {

        Participant target = findParticipant(id);
        participantRepository.save(Participant.builder()
                .id(target.getId())
                .meeting(target.getMeeting())
                .fromId(target.getFromId())
                .toId(target.getToId())
                .status(true)
                .build());
        return target.getMeeting();
    }
}
