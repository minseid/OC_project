package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.constant.MethodType;
import com.example.OC.constant.PlaceStatus;
import com.example.OC.constant.SendType;
import com.example.OC.dto.PlaceAddressDto;
import com.example.OC.entity.*;
import com.example.OC.network.fcm.*;
import com.example.OC.network.response.*;
import com.example.OC.repository.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final FCMService fcmService;
    private final UserMeetingMappingRepository userMeetingMappingRepository;
    private final UserPlaceMappingRepository userPlaceMappingRepository;
    private final String naverMapLink = "nmap://search?query=";

    //장소 조회하는 메서드
    public List<GetPlaceResponse> getplaces(Long meetingId, Long userId) {

        //id 유효성 검사
        Meeting target = findService.valid(meetingRepository.findById(meetingId),EntityType.Meeting);
        User targetUser = findService.valid(userRepository.findById(userId),EntityType.User);
        if(!userMeetingMappingRepository.existsByUserAndMeeting(targetUser, target)) {
            throw new IllegalArgumentException("해당 유저는 해당 모임의 구성원이 아닙니다!");
        }
        List<GetPlaceResponse> places = new ArrayList<>();
        //해당 Meeting에 Place가 있는지 확인
        if(!placeRepository.existsByMeeting(target)) {
            return null;
        }
        placeRepository.findAllByMeeting(target).forEach(place -> {
            Link link = findService.valid(linkRepository.findByPlace(place),EntityType.Link);
            places.add(GetPlaceResponse.builder()
                    .id(place.getId())
                    .meetingId(meetingId)
                    .naverLink(link.getNaverLink())
                    .kakaoLink(link.getKakaoLink())
                    .name(place.getName())
                    .address(place.getAddress())
                    .likeCount(place.getLikeCount())
                    .placeStatus(place.getPlaceStatus())
                    .together(userPlaceMappingRepository.existsByUserAndPlace(targetUser, place) && userPlaceMappingRepository.findAllByPlace(place).size()>1)
                    .build());
        });
        return places;
    }

    //장소를 추가하는 메서드
    public AddPlaceResponse addPlace(Long meetingId, Long userId, String name, String address) {
        log.warn("장소추가시작");
        //각종 id 유효성 확인
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        log.warn(targetMeeting.toString());
        log.warn("유저찾기시작");
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        log.warn(targetUser.toString());
        if(!userMeetingMappingRepository.existsByUserAndMeeting(targetUser, targetMeeting)) {
            throw new IllegalArgumentException("해당 유저는 해당 모임에 속해있지 않습니다!");
        }
        //카카오맵 api 이용해 해당 장소 정보 검색
        PlaceAddressDto placeAddressDto = apiService.getKakaoMapPlaceId(name, address);
        log.warn(placeAddressDto.toString());
        //해당 모임과 좌표를 기준으로 저장되어있는 장소 전부 불러와서 이름 겹치는지 확인
        List<Place> targetPlaces = placeRepository.findAllByXAndYAndMeeting(placeAddressDto.getX(), placeAddressDto.getY(),targetMeeting);
        log.warn(targetPlaces.toString());
        if (targetPlaces.isEmpty()) {
            //해당 좌표 기준으로 저장되어있는 장소 없으면 새로 추가
            return addPlaceResponse(newPlace(targetUser, targetMeeting, name, address, placeAddressDto),false);
        } else {
            //해당 좌표 기준으로 저장되어 있는 장소 중 이름 겹치는 것으로만 필터
            List<Place> places = targetPlaces.stream()
                    .filter(place -> noBlankUpper(place.getName()).contains(noBlankUpper(name)) || noBlankUpper(name).contains(noBlankUpper(place.getName())))
                    .toList();
            if (places.isEmpty()) {
                //해당 이름으로된 장소가 없으므로 새로 추가
                return addPlaceResponse(newPlace(targetUser, targetMeeting, name, address, placeAddressDto),false);
            } else {
                //해당 장소가 있으므로 사용자만 추가
                Place targetPlace = places.get(0);
                //해당장소를 공유한 유저에 targetUser가 있는지 확인
                List<UserPlaceMapping> users = userPlaceMappingRepository.findAllByPlace(targetPlace);
                users.forEach(userPlaceMapping -> {
                    if(userPlaceMapping.getUser() == targetUser) {
                        throw new IllegalArgumentException("해당유저는 똑같은 장소를 이미 공유했습니다!");
                    }
                });
                //해당장소가 있다는거는 이미 공유한 사람이 있다는 것이므로 저장 후 해당 장소를 공유한 사람이 두명이라면 다른사람에게 같이 찾은장소라고 전송
                List<UserPlaceMapping> mappings = userPlaceMappingRepository.findAllByPlace(targetPlace);
                if(mappings.size() == 1) {
                    try {
                        fcmService.sendMessageToken(mappings.get(0).getUser().getId(),null,null,SendTogetherPlaceDto.builder()
                                .placeId(targetPlace.getId())
                                .together(true)
                                .build(),MethodType.PlaceTogether,SendType.Data);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("실시간 데이터 전송 실패! : " + e.getMessage());
                    }
                }
                userPlaceMappingRepository.save(UserPlaceMapping.builder()
                        .user(targetUser)
                        .place(targetPlace)
                        .build());
                return addPlaceResponse(findService.valid(linkRepository.findByPlace(targetPlace),EntityType.Link),true);
            }
        }
    }

    private AddPlaceResponse addPlaceResponse(Link link, boolean together) {
        return AddPlaceResponse.builder()
                .id(link.getPlace().getId())
                .meetingId(link.getPlace().getMeeting().getId())
                .naverLink(link.getNaverLink())
                .kakaoLink(link.getKakaoLink())
                .name(link.getPlace().getName())
                .address(link.getPlace().getAddress())
                .likeCount(link.getPlace().getLikeCount())
                .placeStatus(link.getPlace().getPlaceStatus())
                .together(together)
                .build();
    }

    private Link newPlace(User targetUser, Meeting targetMeeting, String name, String address,PlaceAddressDto placeAddressDto) {
        //새로운 리스트 만들어서 유저추가후 장소저장
        Place savedPlace = placeRepository.save(Place.builder()
                .meeting(targetMeeting)
                .name(name)
                .address(address)
                .x(placeAddressDto.getX())
                .y(placeAddressDto.getY())
                .likeCount(0)
                .placeStatus(PlaceStatus.NotPicked)
                .build());
        userPlaceMappingRepository.save(UserPlaceMapping.builder()
                .user(targetUser)
                .place(savedPlace)
                .build());
        //모임구성원들에게 새로운 장소추가정보 전송
        userMeetingMappingRepository.findAllByMeeting(targetMeeting).forEach(userMeetingMapping -> {
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendAddPlaceDto.builder()
                                .meetingId(targetMeeting.getId())
                                .placeId(savedPlace.getId())
                                .placeName(savedPlace.getName())
                                .address(savedPlace.getAddress())
                                .likeCount(savedPlace.getLikeCount())
                                .placeStatus(savedPlace.getPlaceStatus())
                                .naverLink(naverMapLink + URLEncoder.encode(savedPlace.getName() + " " + placeAddressDto.getDetailAddress()) + "&appname=com.example.audi")
                                .kakaoLink(placeAddressDto.getKakaoLink())
                                .build(),
                        MethodType.PlaceAdd,SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터 전송 실패! : " + e.getMessage());
            }
        });
        //링크정보 저장
        return linkRepository.save(Link.builder()
                .place(savedPlace)
                .naverLink(naverMapLink + URLEncoder.encode(savedPlace.getName() + " " + placeAddressDto.getDetailAddress()) + "&appname=com.example.audi")
                .kakaoLink(placeAddressDto.getKakaoLink())
                .build());
    }

    private String noBlankUpper(String target) {
        return target.trim().toLowerCase();
    }

    //장소삭제 메서드
    public Place deletePlace(Long placeId) {

        //id 유효성 판단
        Place target = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        //해당 장소에 저장되어있는 코멘트 삭제
        commentRepository.findAllByPlace(target).forEach(commentRepository::delete);
        placeRepository.delete(target);
        //모임 구성원들에게 장소삭제정보 전송
        userMeetingMappingRepository.findAllByMeeting(target.getMeeting()).forEach(userMeetingMapping -> {
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null,SendDeletePlaceDto.builder().id(target.getId()).build(),MethodType.PlaceDelete,SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터 전송 실패 : " + e.getMessage());
            }
        });
        return target;
    }

    //장소pick 메서드
    public PickPlaceResponse pickPlace(Long placeId) {

        //id 유효성 판단
        Place target = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        //좋아요수가 모임 멤버수 내에 있는 범위 내에서 +1, -1
        Place saved = placeRepository.save(Place.builder()
                    .id(target.getId())
                    .meeting(target.getMeeting())
                    .name(target.getName())
                    .address(target.getAddress())
                    .likeCount(target.getLikeCount())
                    .placeStatus(target.getPlaceStatus()==PlaceStatus.Picked? PlaceStatus.NotPicked: PlaceStatus.Picked)
                    .build());
        //모임구성원들에게 변경내용 전송
        userMeetingMappingRepository.findAllByMeeting(saved.getMeeting()).forEach(userMeetingMapping -> {
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendPickPlaceDto.builder()
                                .placeId(saved.getId())
                                .placeStatus(saved.getPlaceStatus())
                                .build(),
                        MethodType.PlacePick,SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터 전송 실패! : " + e.getMessage());
            }
        });
        return PickPlaceResponse.builder()
                .id(saved.getId())
                .likeCount(saved.getLikeCount())
                .placeStatus(saved.getPlaceStatus())
                .build();
    }

    //장소 좋아요 메서드
    public PickPlaceResponse likePlace(Long placeId, boolean like) {

        //id 유효성 검증
        Place target = findService.valid(placeRepository.findById(placeId),EntityType.Place);
        //picked 토글
        Place saved = placeRepository.save(Place.builder()
                .id(target.getId())
                .meeting(target.getMeeting())
                .name(target.getName())
                .address(target.getAddress())
                .likeCount(like?(target.getLikeCount()>userMeetingMappingRepository.findAllByMeeting(target.getMeeting()).size()?0:1):(target.getLikeCount()>0?-1:0))
                .placeStatus(target.getPlaceStatus())
                .build());
        //모임구성원들에게 변경내용 전송
        userMeetingMappingRepository.findAllByMeeting(saved.getMeeting()).forEach(userMeetingMapping -> {
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendLikePlaceDto.builder()
                        .placeId(saved.getId())
                        .likeCount(saved.getLikeCount())
                        .build(),
                        MethodType.PlaceLike,SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터 전송 실패! : " + e.getMessage());
            }
        });
        return PickPlaceResponse.builder()
                .id(saved.getId())
                .likeCount(saved.getLikeCount())
                .placeStatus(saved.getPlaceStatus())
                .build();
    }

    //코멘트를 추가하는 메서드
    public AddCommentResponse addComment(Long placeId, Long userId, String description) {

        //각종 id 유효성 검사
        Place targetPlace = findService.valid(placeRepository.findById(placeId), EntityType.Place);
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        //해장 장소를 공유한 사용자가 맞는지 확인
        if(userPlaceMappingRepository.existsByUserAndPlace(targetUser,targetPlace)) {
            //코멘트 추가
            Comment saved = commentRepository.save(Comment.builder()
                    .place(targetPlace)
                    .user(targetUser)
                    .description(description)
                    .build());
            //모임구성원들에게 전송
            userMeetingMappingRepository.findAllByMeeting(targetPlace.getMeeting()).forEach(userMeetingMapping -> {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendAddCommentDto.builder()
                            .placeId(placeId)
                            .commentId(saved.getId())
                            .userId(userId)
                            .description(description)
                            .build(),
                            MethodType.CommentAdd,SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송 실패! : "+ e.getMessage());
                }
            });
            return AddCommentResponse.builder()
                    .placeId(placeId)
                    .commentId(saved.getId())
                    .userId(userId)
                    .description(description)
                    .build();
        } else {
            throw new IllegalArgumentException("해당 장소를 공유한 사용자가 아닙니다!");
        }
    }

    //코멘트 수정
    public EditCommentResponse editComment(Long commentId, Long userId, String description) {

        //id 유효성 확인
        Comment target = findService.valid(commentRepository.findById(commentId), EntityType.Comment);
        //해당코멘트를 단 유저인지 확인
        if(target.getUser().getId().equals(userId)) {
            Comment saved = commentRepository.save(Comment.builder()
                    .id(commentId)
                    .place(target.getPlace())
                    .user(target.getUser())
                    .description(description)
                    .build());
            //모임구성원에게 데이터 전송
            userMeetingMappingRepository.findAllByMeeting(saved.getPlace().getMeeting()).forEach(userMeetingMapping -> {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendEditCommentDto.builder()
                            .commentId(saved.getId())
                            .description(saved.getDescription())
                            .build(),
                            MethodType.CommentEdit,SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송실패! : " + e.getMessage());
                }
            });
            return EditCommentResponse.builder()
                    .id(saved.getId())
                    .description(saved.getDescription())
                    .build();
        } else {
            throw new IllegalArgumentException("이 코멘트를 작성한 유저가 아닙니다!");
        }
    }

    //코멘트 삭제 메서드
    public void deleteComment (Long commentId, Long userId) {

        //id 유효성 검사
        Comment target = findService.valid(commentRepository.findById(commentId), EntityType.Comment);
        if(target.getUser().getId().equals(userId)) {
            commentRepository.delete(target);
            //모임 구성원에게 데이터 전송
            userMeetingMappingRepository.findAllByMeeting(target.getPlace().getMeeting()).forEach(userMeetingMapping -> {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendDeleteCommentDto.builder()
                            .commentId(target.getId())
                            .build(),
                            MethodType.CommentDelete,SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송실패! : " + e.getMessage());
                }
            });
        } else {
            throw new IllegalArgumentException("이 코멘트를 작성한 유저가 아닙니다!");
        }
    }

    //코멘트 조회 메서드
    public List<GetCommentResponse> getComment(Long placeId) {
        List<Comment> target = commentRepository.findAllByPlace(findService.valid(placeRepository.findById(placeId), EntityType.Comment));
        return target.stream().map(GetCommentResponse::toDto).collect(Collectors.toList());
    }

}
