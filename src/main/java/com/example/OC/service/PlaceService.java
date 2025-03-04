package com.example.OC.service;

import com.example.OC.constant.PlaceStatus;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Place;
import com.example.OC.entity.User;
import com.example.OC.repository.LinkRepository;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.PlaceRepository;
import com.example.OC.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@ToString
@Builder
@Transactional
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final LinkRepository linkRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    //같은 장소를 판단하는 알고리즘 추가해야됨
    public Place addPlace(Long meetingId, Long userId, String name, String address) {
        Optional<Meeting> targetMeeting = meetingRepository.findById(meetingId);
        if(targetMeeting.isEmpty()) {
            throw new IllegalArgumentException("모임정보가 올바르지 않습니다!");
        }
        Optional<User> targetUser = userRepository.findById(userId);
        if(targetUser.isEmpty()) {
            throw new IllegalArgumentException("유저정보가 올바르지 않습니다!");
        }
        Optional<Place> target = placeRepository.findByMeetingAndNameAndAddress(targetMeeting.get(), name, address);
        if(target.isPresent())
        {
            List<User> users = target.get().getUser();
            if(users.contains(targetUser.get()))
            {
                throw new IllegalArgumentException("중복된 장소");
            }
            users.add(targetUser.get());
            return placeRepository.save(Place.builder()
                            .id(target.get().getId())
                            .name(name)
                            .meeting(targetMeeting.get())
                            .user(users)
                            .name(name)
                            .address(address)
                            .like_count(target.get().getLike_count())
                            .placeStatus(target.get().getPlaceStatus())
                            .build()
           );
        }else{
            List<User> users = new ArrayList<>();
            users.add(targetUser.get());
            //여기에 링크생성하는 코드 넣어야됨
            return placeRepository.save(Place.builder()
                    .meeting(targetMeeting.get())
                    .user(users)
                    .name(name)
                    .address(address)
                    .like_count(0)
                    .placeStatus(PlaceStatus.NotPicked)
                    .build());
        }
    }

    public Place deletePlace(Long placeId, Long meetingId) {
        Optional<Meeting> targetMeeting = meetingRepository.findById(meetingId);
        if(targetMeeting.isEmpty()) {
            throw new IllegalArgumentException("모임정보가 올바르지 않습니다!");
        }
        Optional<Place> target = placeRepository.findById(placeId);
        if(target.isEmpty()) {
            throw new IllegalArgumentException("장소정보가 올바르지 않습니다!");
        } else if(target.get().getMeeting().equals(targetMeeting.get())) {
            placeRepository.delete(target.get());
            return target.get();
        } else {
            throw new IllegalArgumentException("해당 모임에는 이 장소가 없습니다!");
        }
    }

    public Place pickPlace(Long placeId, Long meetingId) {
        Optional<Meeting> targetMeeting = meetingRepository.findById(meetingId);
        if(targetMeeting.isEmpty()) {
            throw new IllegalArgumentException("모임정보가 올바르지 않습니다!");
        }
        Optional<Place> target = placeRepository.findById(placeId);
        if(target.isEmpty()) {
            throw new IllegalArgumentException("장소정보가 올바르지 않습니다!");
        } else if(target.get().getMeeting().equals(targetMeeting.get())) {
            Place picked = placeRepository.save(Place.builder()
                    .id(target.get().getId())
                    .meeting(targetMeeting.get())
                    .user(target.get().getUser())
                    .name(target.get().getName())
                    .address(target.get().getAddress())
                    .like_count(target.get().getLike_count()+1)
                    .placeStatus(target.get().getPlaceStatus())
                    .build());
            return picked;
        } else {
            throw new IllegalArgumentException("해당 모임에는 이 장소가 없습니다!");
        }
    }
}
