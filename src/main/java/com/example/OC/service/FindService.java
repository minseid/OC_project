package com.example.OC.service;

import com.example.OC.entity.*;
import com.example.OC.repository.*;
import jakarta.persistence.MappedSuperclass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@MappedSuperclass
public class FindService {

    protected final CommentRepository commentRepository;
    protected final FriendRepository friendRepository;
    protected final InquiryRepository inquiryRepository;
    protected final LinkRepository linkRepository;
    protected final MeetingRepository meetingRepository;
    protected final NoticeRepository noticeRepository;
    protected final ParticipantRepository participantRepository;
    protected final PlaceRepository placeRepository;
    protected final ScheduleRepository scheduleRepository;
    protected final UserMeetingMappingRepository userMeetingMappingRepository;
    protected final UserRepository userRepository;

    protected User findUser(Long id) {
        Optional<User> target = userRepository.findById(id);
        if(target.isEmpty()){
            throw new IllegalArgumentException("유저정보가 올바르지 않습니다!");
        } else {
            return target.get();
        }
    }

    protected Meeting findMeeting(Long id) {
        Optional<Meeting> target = meetingRepository.findById(id);
        if(target.isEmpty()){
            throw new IllegalArgumentException("모임정보가 올바르지 않습니다!");
        } else {
            return target.get();
        }
    }

    protected Place findPlace(Long id) {
        Optional<Place> target = placeRepository.findById(id);
        if(target.isEmpty()){
            throw new IllegalArgumentException("장소정보가 올바르지 않습니다!");
        } else {
            return target.get();
        }
    }

    protected Comment findComment(Long id) {
        Optional<Comment> target = commentRepository.findById(id);
        if(target.isEmpty()) {
            throw new IllegalArgumentException("코멘트 정보가 올바르지 않습니다!");
        } else {
            return target.get();
        }
    }

    protected Friend findFriend(Long id) {
        return null;
    }
}
