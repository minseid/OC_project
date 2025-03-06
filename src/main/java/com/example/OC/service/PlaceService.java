package com.example.OC.service;

import com.example.OC.constant.PlaceStatus;
import com.example.OC.entity.Comment;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Place;
import com.example.OC.entity.User;
import com.example.OC.network.response.GetCommentResponse;
import com.example.OC.repository.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@ToString
@Builder
@Transactional
@Slf4j
public class PlaceService extends FindService {

    //같은 장소를 판단하는 알고리즘 추가해야됨
    public Place addPlace(Long meetingId, Long userId, String name, String address) {
        Meeting targetMeeting = findMeeting(meetingId);
        User targetUser = findUser(userId);
        Optional<Place> target = placeRepository.findByMeetingAndNameAndAddress(targetMeeting, name, address);
        if(target.isPresent())
        {
            List<User> users = target.get().getUser();
            if(users.contains(targetUser))
            {
                throw new IllegalArgumentException("중복된 장소");
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
                            .build()
           );
        }else{
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
        Place target = findPlace(placeId);
        placeRepository.delete(target);
        return target;
    }

    public Place pickPlace(Long placeId, Long meetingId) {

        Place target = findPlace(placeId);
        Place picked = placeRepository.save(Place.builder()
                    .id(target.getId())
                    .meeting(findMeeting(meetingId))
                    .user(target.getUser())
                    .name(target.getName())
                    .address(target.getAddress())
                    .like_count(target.getLike_count()+1)
                    .placeStatus(target.getPlaceStatus())
                    .build());
            return picked;
    }

    public Comment addComment(Long placeId, Long userId, String description) {

        Place targetPlace = findPlace(placeId);
        User targetUser = findUser(userId);
        Comment saved = Comment.builder()
                .place(targetPlace)
                .user(targetUser)
                .description(description)
                .build();
        return commentRepository.save(saved);
    }

    public Comment editComment(Long commentId, Long userId, String description) {

        Comment target = findComment(commentId);
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
        Comment target = findComment(commentId);
        if(target.getUser().getId().equals(userId)) {
            commentRepository.delete(target);
            return target;
        } else {
            throw new IllegalArgumentException("이 코멘트를 작성한 유저가 아닙니다!");
        }
    }

    public List<GetCommentResponse> getComment(Long placeId) {
        List<Comment> target = commentRepository.findAllByPlace(findPlace(placeId));
        return target.stream().map(GetCommentResponse::toDto).collect(Collectors.toList());
    }

}
