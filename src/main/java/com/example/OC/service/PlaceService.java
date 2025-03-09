package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.constant.PlaceStatus;
import com.example.OC.entity.Comment;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Place;
import com.example.OC.entity.User;
import com.example.OC.network.response.GetCommentResponse;
import com.example.OC.network.response.KakaoMapApiResponse;
import com.example.OC.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Api;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlaceService {
    
    private final FindService findService;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final CommentRepository commentRepository;
    private final ApiService apiService;

    //같은 장소를 판단하는 알고리즘 추가해야됨
    public Place addPlace(Long meetingId, Long userId, String name, String address, String naverLink) {
        //meetingId 유효성 확인 및 겹치는거 확인
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        try {
            Optional<Place> target = placeRepository.findByMeetingAndNameAndAddress(targetMeeting, name, address);
            if(target.isPresent()) {
                List<User> users = target.get().getUser();
                if(users.contains(targetUser))
                {
                    throw new IllegalArgumentException("중복된 장소를 공유했습니다!");
                }
                users.add(targetUser);
                return placeRepository.save(Place.builder()
                        .id(target.get().getId())
                        .name(name)
                        .meeting(targetMeeting)
                        .user(users)
                        .name(name)
                        .address(address)
                        .like_count(target.get().getLike_count())
                        .placeStatus(target.get().getPlaceStatus())
                        .build());
            } else {
                List<User> newUser = new ArrayList<>();
                newUser.add(targetUser);
                return placeRepository.save(Place.builder()
                        .name(name)
                        .meeting(targetMeeting)
                        .build());
            }
        } catch (IllegalStateException e) {

            List<User> users = new ArrayList<>();
            users.add(targetUser);
            //여기에 링크생성하는 코드 넣어야됨
            return placeRepository.save(Place.builder()
                    .meeting(targetMeeting)
                    .user(users)
                    .name(name)
                    .address(address)
                    .like_count(0)
                    .placeStatus(PlaceStatus.NotPicked)
                    .build());
        }
    }

    public Place deletePlace(Long placeId) {
        Place target = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        placeRepository.delete(target);
        return target;
    }

    public Place pickPlace(Long placeId, Long meetingId) {

        Place target = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        Place picked = placeRepository.save(Place.builder()
                    .id(target.getId())
                    .meeting(findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting))
                    .user(target.getUser())
                    .name(target.getName())
                    .address(target.getAddress())
                    .like_count(target.getLike_count()+1)
                    .placeStatus(target.getPlaceStatus())
                    .build());
            return picked;
    }

    public Comment addComment(Long placeId, Long userId, String description) {

        Place targetPlace = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        Comment saved = Comment.builder()
                .place(targetPlace)
                .user(targetUser)
                .description(description)
                .build();
        return commentRepository.save(saved);
    }

    public Comment editComment(Long commentId, Long userId, String description) {

        Comment target = findService.valid(commentRepository.findById(commentId), EntityType.Comment);
        if(target.getUser().getId().equals(userId)) {
            return commentRepository.save(Comment.builder()
                            .id(commentId)
                            .place(target.getPlace())
                            .user(target.getUser())
                            .description(description)
                            .build());
        } else {
            throw new IllegalArgumentException("이 코멘트를 작성한 유저가 아닙니다!");
        }
    }

    public Comment deleteComment (Long commentId,Long userId) {
        Comment target = findService.valid(commentRepository.findById(commentId), EntityType.Comment);
        if(target.getUser().getId().equals(userId)) {
            commentRepository.delete(target);
            return target;
        } else {
            throw new IllegalArgumentException("이 코멘트를 작성한 유저가 아닙니다!");
        }
    }

    public List<GetCommentResponse> getComment(Long placeId) {
        List<Comment> target = commentRepository.findAllByPlace(findService.valid(placeRepository.findById(placeId), EntityType.Comment));
        return target.stream().map(GetCommentResponse::toDto).collect(Collectors.toList());
    }

}
