package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.constant.PlaceStatus;
import com.example.OC.dto.PlaceAddressDto;
import com.example.OC.entity.*;
import com.example.OC.network.response.GetCommentResponse;
import com.example.OC.repository.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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
    private final LinkRepository linkRepository;
    private final String naverMapLink = "nmap://search?query=";

    //같은 장소를 판단하는 알고리즘 추가해야됨
    public Link addPlace(Long meetingId, Long userId, String name, String address, String naverLink) {
        //meetingId 유효성 확인 및 겹치는거 확인
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        PlaceAddressDto placeAddressDto = apiService.getKakaoMapPlaceId(name,address);
        List<Place> targetPlaces = placeRepository.findAllByXAndY(placeAddressDto.getX(), placeAddressDto.getY());
        if(targetPlaces.isEmpty()) {
            List<User> users = new ArrayList<>();
            users.add(targetUser);
            Place savedPlace = placeRepository.save(Place.builder()
                    .meeting(targetMeeting)
                    .user(users)
                    .name(name)
                    .address(address)
                    .x(placeAddressDto.getX())
                    .y(placeAddressDto.getY())
                    .like_count(0)
                    .placeStatus(PlaceStatus.NotPicked)
                    .build());
            if(naverLink != null) {
                return linkRepository.save(Link.builder()
                            .place(savedPlace)
                            .naverLink(naverLink)
                            .kakaoLink(placeAddressDto.getKakaoLink())
                            .build());
            } else {
                return linkRepository.save(Link.builder()
                        .place(savedPlace)
                        .naverLink(naverMapLink + URLEncoder.encode(savedPlace.getName() + " " + placeAddressDto.getDetailAddress()) + "&appname=com.example.audi")
                        .kakaoLink(placeAddressDto.getKakaoLink())
                        .build());
            }
        } else {
            //여기서부터 하기
            return null;
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
