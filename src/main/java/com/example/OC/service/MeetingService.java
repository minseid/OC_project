package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.constant.MethodType;
import com.example.OC.constant.SendType;
import com.example.OC.entity.*;
import com.example.OC.network.fcm.SendDeleteFriendDto;
import com.example.OC.network.fcm.SendEditMeetingDto;
import com.example.OC.network.fcm.SendNewMemberDto;
import com.example.OC.network.response.GetParticipantsResponse;
import com.example.OC.repository.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    
    private final FindService findService;
    private final AwsS3Service awsS3Service;
    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final UserMeetingMappingRepository userMeetingMappingRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;
    private final PlaceRepository placeRepository;
    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;
    private final FriendRepository friendRepository;

    private final String linkForInvite = "https://www.audi.com/";

    //모임 초대용 링크 만드는 메서드
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
            //만든링크가 중복되는지 검사
            if (!meetingRepository.existsByLink(randomCode)) {
                return linkForInvite + randomCode;
            }
        }
    }

    //모임 만드는 메서드
    public Meeting addMeeting(String title, String description, MultipartFile image, Long fromId, List<Long> inviteList ) {

        //fromId가 유효한지 확인
        if(userRepository.existsById(fromId)) {
            //이미지 저장할때 meetingId가 필요한데 id를 DB에서 자동관리하므로 하나 생성하고 그 엔티티를 수정하는 방식을 사용
            Meeting meetingForImage = meetingRepository.save(Meeting.builder().title(title).description(description).finished(false).build());
            Meeting target = Meeting.builder()
                    .id(meetingForImage.getId())
                    .title(title)
                    .description(description)
                    .link(makeLink())
                    .image(image.isEmpty()?null:awsS3Service.saveMeetingImage(image, meetingForImage.getId()))
                    .finished(false)
                    .build();
            //초대시작
            inviteList.forEach(toId -> {
                if(userRepository.existsById(toId)) {
                    participantRepository.save(Participant.builder()
                            .meeting(target)
                            .fromId(fromId)
                            .toId(toId)
                            .status(false)
                            .build());
                    try {
                        fcmService.sendMessageToken(toId, "모임 초대!", findService.valid(userRepository.findById(fromId), EntityType.User).getName()+"님이 " + target.getTitle() + "모임에 초대하셨어요!", null,null, SendType.Notification);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("초대전송 실패! : " + e.getMessage());
                    }
                } else {
                    throw new IllegalArgumentException("초대를 받는 유저 정보가 올바르지 않습니다!");
                }
            });
            //모임-유저연결
            userMeetingMappingRepository.save(UserMeetingMapping.builder()
                    .meeting(target)
                    .user(findService.valid(userRepository.findById(fromId), EntityType.User))
                    .build());
            return meetingRepository.save(target);
        } else {
            throw new IllegalArgumentException("보내는 유저 정보가 올바르지 않습니다!");
        }
    }

    //모임 수정하는 메서드
    public Meeting editMeeting(Long id, String title, String description, MultipartFile image, boolean finished) {

        if(title==null && description==null && image.isEmpty()) {
            throw new IllegalArgumentException("수정사항이 없습니다!");
        }
        //먼저 해당 id로 모임이 존재하는지 확인
        Meeting targetmeeting = findService.valid(meetingRepository.findById(id), EntityType.Meeting);
        Meeting target = Meeting.builder()
                .id(id)
                .title(title == null? targetmeeting.getTitle():title)
                .description(description==null?targetmeeting.getDescription():description)
                .link(targetmeeting.getLink())
                //이미지를 수정한다면 기존에 있는것은 삭제 후 새로 저장, 이미지 수정이 없다면 그대로
                .image(image.isEmpty()? targetmeeting.getImage(): awsS3Service.editMeetingImage(image, id, targetmeeting.getLink()))
                .finished(targetmeeting.isFinished())
                .build();
        meetingRepository.save(target);
        /*
        모임수정하는것도 실시간으로 정보 보내야 된다면 주석 해제하기
        userMeetingMappingRepository.findAllByMeeting(target).forEach(userMeetingMapping -> {
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendEditMeetingDto.builder()
                        .meetingId(target.getId())
                        .title(target.getTitle())
                        .description(target.getDescription())
                        .image(target.getImage())
                        .finished(target.isFinished())
                        .build(),
                       MethodType.MeetingEdit,SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터전송 실패! : " + e.getMessage());
            }
        });
         */
        return target;
    }

    //모임 탈퇴 메서드
    public void quitMeeting(Long userId, Long meetingId) {

        //모임 구성원삭제
        userMeetingMappingRepository.delete(findService.valid(userMeetingMappingRepository.findByUserAndMeeting(findService.valid(userRepository.findById(userId),EntityType.User),findService.valid(meetingRepository.findById(meetingId),EntityType.Meeting)),EntityType.UserMeetingMapping));
        //모임 id 유효성검사
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        //친구목록에서 모임삭제
        friendRepository.findAllByU1OrU2(userId,userId).forEach(friend -> {
            List<Long> meets = friend.getMeets();
            meets.removeIf(id -> id==meetingId);
            if(meets.isEmpty()) {
                //친구목록에서 겹친 모임이 해당모임만 있다면 친구도 삭제
                try {
                    fcmService.sendMessageToken(friend.getU1()==userId?friend.getU2():userId,null,null, SendDeleteFriendDto.builder().userId(userId).build(),MethodType.FriendDelete,SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송 실패! : "+e.getMessage());
                }
                friendRepository.delete(friend);
            } else {
                //다른모임도 있다면 해당 모임만 삭제
                friendRepository.save(Friend.builder()
                                .id(friend.getId())
                                .u1(friend.getU1())
                                .u2(friend.getU2())
                                .u1Bookmark(friend.isU1Bookmark())
                                .u2Bookmark(friend.isU2Bookmark())
                                .meets(meets)
                                .build());
            }
        });
        //모임구성원이 없다면 모임 삭제
        //모임삭제전 해당 모임과 연관된 모든 데이터 삭제
        if(userMeetingMappingRepository.findAllByMeeting(targetMeeting).isEmpty()) {
            //초대삭제
            participantRepository.findAllByMeeting(targetMeeting).forEach(participantRepository::delete);
            //장소삭제전에 장소와 관련된 모든 데이터 삭제
            placeRepository.findAllByMeeting(targetMeeting).forEach(place -> {
                //장소와 관련된 코멘트 삭제
                commentRepository.findAllByPlace(place).forEach(commentRepository::delete);
            });
            //일정삭제
            scheduleRepository.delete(findService.valid(scheduleRepository.findByMeeting(targetMeeting),EntityType.Schedule));
            meetingRepository.delete(targetMeeting);
        }
    }

    //초대 현황 조회 메서드
    public List<GetParticipantsResponse> getParticipants(Long meetingId) {

        List<GetParticipantsResponse> participantsResponses = new ArrayList<>();
        //해당 meetingId 유효한지 검사
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        //해당 모임으로 조회되는 모든 초대 조회
        List<Participant> participants = participantRepository.findAllByMeeting(targetMeeting);
        //초대에서 필요한 정보만 추출
        participants.forEach(participant -> {
            Long fromId = participant.getFromId();
            Long toId = participant.getToId();
            participantsResponses.add(GetParticipantsResponse.builder()
                            .fromId(fromId)
                            .fromName(findService.valid(userRepository.findById(fromId), EntityType.User).getName())
                            .toId(toId)
                            .toName(findService.valid(userRepository.findById(toId), EntityType.User).getName())
                            .status(participant.isStatus())
                            .build());
        });
        return participantsResponses;
    }

    //해당 유저가 참여중인 모임 조회
    public List<Meeting> getMeetings(Long userId) {

        //해당 userId가 유효한지 검사
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        List<UserMeetingMapping> userMeetingMappings = userMeetingMappingRepository.findByUser(targetUser);
        /*
        List<Meeting> meetings = new ArrayList<>();
        userMeetingmappings.forEach(userMeetingMapping -> meetings.add(userMeetingMapping.getMeeting()));
        return meetings인데 아래걸로 바꿈 공부하기
         */
        //userMeetingMappings의 모든 모임 추출
        return userMeetingMappings.stream()
                .map(UserMeetingMapping::getMeeting)
                .toList();
    }

    //초대생성메서드
    public void addParticipant(Long meetingId, Long fromId, Long toId) {

        //각종 id가 유효한지 검사
        findService.valid(userRepository.findById(fromId), EntityType.User);
        findService.valid(userRepository.findById(toId), EntityType.User);
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        //초대생성
        Participant saved = participantRepository.save(Participant.builder()
                .meeting(targetMeeting)
                .fromId(fromId)
                .toId(toId)
                .status(false)
                .build());
        //fcm으로 알림전송
        try {
            fcmService.sendMessageToken(toId, "모임 초대!", findService.valid(userRepository.findById(fromId), EntityType.User).getName()+"님이 " + targetMeeting.getTitle() + "모임에 초대하셨어요!", null,null, SendType.Notification);
        } catch (IOException e) {
            throw new IllegalArgumentException("초대전송 실패! : " + e.getMessage());
        }
    }

    //초대수락메서드
    public Meeting inviteOk(Long id) {

        //id 유효한지 검사
        Participant target = findService.valid(participantRepository.findById(id), EntityType.Participant);
        User acceptUser = findService.valid(userRepository.findById(target.getToId()), EntityType.User);
        Meeting targetMeeting = target.getMeeting();
        //초대상태 변경
        participantRepository.save(Participant.builder()
                .id(target.getId())
                .meeting(targetMeeting)
                .fromId(target.getFromId())
                .toId(target.getToId())
                .status(true)
                .build());
        //해당 모임 구성원에게 추가된 구성원정보 전송
        userMeetingMappingRepository.findAllByMeeting(targetMeeting).forEach(userMeetingMapping -> {
           try {
               fcmService.sendMessageToken(userMeetingMapping.getUser().getId(), null, null, SendNewMemberDto.builder()
                       .meetingId(targetMeeting.getId())
                       .userId(acceptUser.getId())
                       .userName(acceptUser.getName())
                       .userImage(acceptUser.getProfileImage())
                       .build(), MethodType.MeetingAccept, SendType.Data);
           } catch (IOException e) {
               throw new IllegalArgumentException("실시간 데이터전송 실패! : " + e.getMessage());
           }
        });
        //해당 모임에 구성원 추가
        userMeetingMappingRepository.save(UserMeetingMapping.builder()
                .user(acceptUser)
                .meeting(targetMeeting)
                .build());
        return targetMeeting;
    }
}
