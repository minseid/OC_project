package com.where.service;

import com.where.constant.EntityType;
import com.where.entity.Friend;
import com.where.entity.Meeting;
import com.where.entity.User;
import com.where.network.response.BookmarkFriendResponse;
import com.where.network.response.GetFriendResponse;
import com.where.network.response.MeetingForFriendResponse;
import com.where.repository.FriendRepository;
import com.where.repository.MeetingRepository;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final FriendRepository friendRepository;
    private final FindService findService;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    //친구목록 가져오는 메서드
    public List<GetFriendResponse> getFriend(Long id) {

        List<GetFriendResponse> responses = new ArrayList<>();
        friendRepository.findAllByUser(id).forEach(friend -> {
            //모임정보 담기
            List<MeetingForFriendResponse> data = new ArrayList<>();
            friend.getMeets().forEach(meetingId -> {
                Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
                data.add(MeetingForFriendResponse.builder()
                        .meetingId(meetingId)
                        .meetingImage(targetMeeting.getImage())
                        .meetingName(targetMeeting.getTitle())
                        .description(targetMeeting.getDescription())
                        .date(targetMeeting.getUpdatedAt().toLocalDate())
                        .build());
            });
            //해당유저id가 U1에 있는지 U2에 있는지 모름
            User targetUser = null;
            if(friend.getU1()==id) {
                targetUser = findService.valid(userRepository.findById(friend.getU2()), EntityType.User);
            } else {
                targetUser = findService.valid(userRepository.findById(friend.getU1()), EntityType.User);
            }
            responses.add(GetFriendResponse.builder()
                    .friendId(targetUser.getId())
                    .friendName(targetUser.getName())
                    .friendImage(targetUser.getProfileImage())
                    .meetingDetail(data)
                    .build());
        });
        return responses;
    }

    //친구삭제 메서드
    public void deleteFriend(Long userId, Long friendId) {

        if(friendRepository.existsByU1AndU2(userId, friendId)) {
            friendRepository.deleteByU1AndU2(userId, friendId);
        } else if(friendRepository.existsByU1AndU2(friendId, userId)){
            friendRepository.deleteByU1AndU2(friendId, userId);
        } else {
            throw new IllegalArgumentException("해당 친구를 찾을수 없습니다!");
        }
    }

    //친구 북마크 메서드
    public BookmarkFriendResponse bookmarkFriend(Long userId, Long friendId) {
        if(friendRepository.existsByU1AndU2(userId, friendId)) {
            Friend target = friendRepository.findByU1AndU2(userId, friendId).get();
            friendRepository.save(Friend.builder()
                    .id(target.getId())
                    .u1(target.getU1())
                    .u2(target.getU2())
                    .u1Bookmark(!target.isU1Bookmark())
                    .u2Bookmark(target.isU2Bookmark())
                    .meets(target.getMeets())
                    .build());
            return BookmarkFriendResponse.builder()
                    .userId(userId)
                    .friendId(friendId)
                    .bookmark(!target.isU1Bookmark())
                    .build();
        } else if(friendRepository.existsByU1AndU2(friendId, userId)){
            Friend target = friendRepository.findByU1AndU2(friendId, userId).get();
            friendRepository.save(Friend.builder()
                    .id(target.getId())
                    .u1(target.getU1())
                    .u2(target.getU2())
                    .u1Bookmark(target.isU1Bookmark())
                    .u2Bookmark(!target.isU2Bookmark())
                    .meets(target.getMeets())
                    .build());
            return BookmarkFriendResponse.builder()
                    .userId(userId)
                    .friendId(friendId)
                    .bookmark(!target.isU2Bookmark())
                    .build();
        } else {
            throw new IllegalArgumentException("해당 친구를 찾을수 없습니다!");
        }
    }
}
