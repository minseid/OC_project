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
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final UserMeetingMappingRepository userMeetingMappingRepository;
    private final UserRepository userRepository;

    boolean flag = false;
    String reception = "";

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

    public Meeting addMeeting(String title, String description, String image,Long fromId, List<Long> friends ) {
        Optional<User> saveUser = userRepository.findById(fromId);
        if(saveUser.isEmpty()) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }
        Meeting target = Meeting.builder()
                .title(title)
                .description(description)
                .link(makeLink())
                .image(image==null?null:image)
                .build();
        friends.forEach(id -> participantRepository.save(Participant.builder()
                .meeting(target)
                .fromId(fromId)
                .toId(id)
                .status(false)
                .build()));
        userMeetingMappingRepository.save(UserMeetingMapping.builder()
                .meeting(target)
                .user(saveUser.get())
                .build());
        //초대관련 넣어야됨
        return meetingRepository.save(target);
    }

    public Meeting editMeeting(Long id, String title, String description, String image) {
        Optional<Meeting> current = meetingRepository.findById(id);
        if(current.isEmpty()){
            throw new IllegalArgumentException("모임id가 올바르지 않습니다.");
        }
        Meeting target = Meeting.builder()
                .id(id)
                .title(title)
                .description(description)
                .link(current.get().getLink())
                .image(image==null?null:image)
                .build();
        return meetingRepository.save(target);
    }

    public Meeting quitMeeting(Long userId, Long meetingId) {
        Optional<User> targetUser = userRepository.findById(userId);
        if(targetUser.isEmpty()){
            throw new IllegalArgumentException("유저정보가 올바르지 않습니다!");
        }
        Optional<Meeting> targetMeeting = meetingRepository.findById(meetingId);
        if(targetMeeting.isEmpty()){
            throw new IllegalArgumentException("모임정보고 올바르지 않습니다!");
        }
        Optional<UserMeetingMapping> target = userMeetingMappingRepository.findByUserAndMeeting(targetUser.get(),targetMeeting.get());
        if(target.isEmpty()) {
            throw new IllegalArgumentException("해당유저는 해당모임에 가입되어있지 않습니다!");
        }
        userMeetingMappingRepository.delete(target.get());
        return targetMeeting.get();
    }

    public List<Participant> loadParticipants(Long meetingId) {
        Optional<Meeting> targetMeeting = meetingRepository.findById(meetingId);
        if(targetMeeting.isEmpty()){
            throw new IllegalArgumentException("올바르지않은 모임ID입니다!");
        }
        return participantRepository.findAllByMeeting(targetMeeting.get());
    }

    public List<Meeting> getMeetings(Long userId) {
        Optional<User> targetUser = userRepository.findById(userId);
        if(targetUser.isEmpty()){
            throw new IllegalArgumentException("유저를 찾을수 없습니다!");
        }
        return userMeetingMappingRepository.findByUser(targetUser.get());
    }
}
