package com.where.service;

import com.where.constant.EntityType;
import com.where.constant.MethodType;
import com.where.constant.SendType;
import com.where.entity.*;
import com.where.network.fcm.SendAddMemberDto;
import com.where.network.fcm.SendDeleteFriendDto;
import com.where.network.response.*;
import com.where.repository.*;
import com.where.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private final LinkRepository linkRepository;
    private final UserPlaceMappingRepository userPlaceMappingRepository;

    //매일 00시마다 모임종료3개월 이상은 삭제
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteExpiredMeetings() {
        log.info("모임종료3개월이상 모임 삭제 진행");
        List<Meeting> targetMeetings = meetingRepository.findAllByFinishedIsTrueAndUpdatedAtBefore(LocalDateTime.now().minusMonths(3l));
        targetMeetings.forEach(meeting -> {
            //해당 모임에 있는 구성원들 전부 조회후 친구에서 meetings안에서 해당 meetingId 제거
            List<UserMeetingMapping> targetMapping =  userMeetingMappingRepository.findAllByMeeting(meeting);
            List<User> users = targetMapping.stream().map(UserMeetingMapping::getUser).toList();
            //구성원정보 삭제
            userMeetingMappingRepository.deleteAll(targetMapping);
            //구성원이 2명 이상일때만 친구에서 모임내역삭제
            if(users.size() > 1) {
                //구성원들로 구성된 combination 생성(nCr 형식)
                List<Pair<User, User>> userPairs = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    for (int j = i + 1; j < users.size(); j++) { // j = i + 1로 중복 제거
                        userPairs.add(new Pair<>(users.get(i), users.get(j))); // 사용자 간의 쌍 추가
                    }
                }
                userPairs.forEach(pair -> {
                    Optional<Friend> friends = friendRepository.findByUserSet(pair.getFirst().getId(), pair.getSecond().getId());
                    if(friends.isPresent()) {
                        //해당 친구목록에서 만난모임에 해당 모임이 있는지 확인
                        if(friends.get().getMeets().contains(meeting.getId())) {
                            friends.get().getMeets().remove(meeting.getId());
                            //친구에서 만난모임이 해당 모임만 있다면 친구삭제
                            if(friends.get().getMeets().isEmpty()) {
                                friendRepository.delete(friends.get());
                            } else {
                                friendRepository.save(friends.get());
                            }
                        } else {
                            log.error("친구목록에 해당 모임이 없습니다.");
                        }
                    }
                });
            }
            //초대삭제
            participantRepository.deleteAllByMeeting(meeting);
            //장소삭제전에 장소와 관련된 모든 데이터 삭제
            placeRepository.findAllByMeeting(meeting).forEach(place -> {
                //코멘트삭제
                commentRepository.deleteAllByPlace(place);
                //링크삭제
                linkRepository.deleteByPlace(place);
                //장소-유저매핑삭제
                userPlaceMappingRepository.deleteAllByPlace(place);
                //장소삭제
                placeRepository.delete(place);
            });
            //일정삭제
            if(scheduleRepository.existsByMeeting(meeting)) {
                scheduleRepository.deleteByMeeting(meeting);
            }
            //모임삭제
            meetingRepository.delete(meeting);
        });
        log.info("모임종료3개월이상 모임 삭제 완료 : " + targetMeetings.size() + "개");
    }

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
    public AddMeetingResponse addMeeting(String title, String description, MultipartFile image, Long fromId, List<Long> inviteList ) {

        //fromId가 유효한지 확인
        if(userRepository.existsById(fromId)) {
            //이미지 저장할때 meetingId가 필요한데 id를 DB에서 자동관리하므로 하나 생성하고 그 엔티티를 수정하는 방식을 사용
            Meeting meetingForImage = meetingRepository.save(Meeting.builder().title(title).description(description).finished(false).build());
            Meeting target = Meeting.builder()
                    .id(meetingForImage.getId())
                    .title(title)
                    .description(description)
                    .link(makeLink())
                    .image((image == null || image.isEmpty())?null:awsS3Service.saveMeetingImage(image, meetingForImage.getId()))
                    .finished(false)
                    .build();
            //초대시작
            if(inviteList!=null && !inviteList.isEmpty()){
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
                        throw new IllegalArgumentException("초대를 받는 유저 정보가 올바르지 않습니다! : " + toId);
                    }
                });
            }
            //모임-유저연결
            userMeetingMappingRepository.save(UserMeetingMapping.builder()
                    .meeting(target)
                    .user(findService.valid(userRepository.findById(fromId), EntityType.User))
                    .build());
            Meeting saved = meetingRepository.save(target);
            log.info(target.toString());
            return AddMeetingResponse.builder()
                    .id(saved.getId())
                    .title(saved.getTitle())
                    .description(saved.getDescription())
                    .link(saved.getLink())
                    .image(saved.getImage())
                    .createdAt(saved.getCreatedAt())
                    .build();
        } else {
            throw new IllegalArgumentException("모임을 만드는 유저 정보가 올바르지 않습니다!");
        }
    }

    //모임 수정하는 메서드
    public EditMeetingResponse editMeeting(Long id, String title, String description, Long userId, MultipartFile image) {

        if(title==null && description==null && (image == null ||image.isEmpty())) {
            throw new IllegalArgumentException("수정사항이 없습니다!");
        }
        //먼저 해당 id로 모임이 존재하는지 확인
        Meeting targetmeeting = findService.valid(meetingRepository.findById(id), EntityType.Meeting);
        //해당유저가 모임에 속해있는지 확인
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        if(!userMeetingMappingRepository.existsByUserAndMeeting(targetUser, targetmeeting)) {
            throw new IllegalArgumentException("해당 유저는 해당 모임에 속해있지 않습니다!");
        }
        if(targetmeeting.isFinished()) {
            throw new IllegalArgumentException("해당모임은 종료되었습니다!");
        }
        Meeting target = Meeting.builder()
                .id(id)
                .title(title == null? targetmeeting.getTitle():title)
                .description(description==null?targetmeeting.getDescription():description)
                .link(targetmeeting.getLink())
                //이미지를 수정한다면 기존에 있는것은 삭제 후 새로 저장, 이미지 수정이 없다면 그대로
                .image(image.isEmpty()? targetmeeting.getImage(): awsS3Service.editMeetingImage(image, id, targetmeeting.getImage()))
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
        return EditMeetingResponse.builder()
                .id(target.getId())
                .title(target.getTitle())
                .description(target.getDescription())
                .link(target.getLink())
                .image(target.getImage())
                .build();
    }

    //모임종료메서드
    public void finishMeeting(Long meetingId, Long userId) {

        //id 유효성확인
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        //모임이 종료되었으면 오류세메지 전송
        if(targetMeeting.isFinished()) {
            throw new IllegalArgumentException("해당 모임은 종료되었습니다!");
        }
        if(userMeetingMappingRepository.existsByUserAndMeeting(targetUser, targetMeeting)) {
            meetingRepository.save(Meeting.builder()
                    .id(targetMeeting.getId())
                    .title(targetMeeting.getTitle())
                    .description(targetMeeting.getDescription())
                    .image(targetMeeting.getImage())
                    .link(targetMeeting.getLink())
                    .finished(true)
                    .build());
        } else {
            throw new IllegalArgumentException("해당유저는 해당모임의 구성원이 아닙니다!");
        }
    }

    //모임 탈퇴 메서드
    public void quitMeeting(Long userId, Long meetingId) {

        //모임 id 유효성검사
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        if(targetMeeting.isFinished()) {
            throw new IllegalArgumentException("해당 모임은 종료되었습니다!");
        }
        //모임 구성원삭제
        userMeetingMappingRepository.delete(findService.valid(userMeetingMappingRepository.findByUserAndMeeting(findService.valid(userRepository.findById(userId),EntityType.User),findService.valid(meetingRepository.findById(meetingId),EntityType.Meeting)),EntityType.UserMeetingMapping));
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
            participantRepository.deleteAllByMeeting(targetMeeting);
            //장소삭제전에 장소와 관련된 모든 데이터 삭제
            placeRepository.findAllByMeeting(targetMeeting).forEach(place -> {
                //코멘트 삭제
                commentRepository.deleteAllByPlace(place);
                //링크삭제
                linkRepository.deleteByPlace(place);
                //장소-유저매핑삭제
                userPlaceMappingRepository.deleteAllByPlace(place);
                //장소삭제
                placeRepository.delete(place);
            });
            //일정삭제
            if(scheduleRepository.existsByMeeting(targetMeeting)) {
                scheduleRepository.deleteByMeeting(targetMeeting);
            }
            //모임삭제
            meetingRepository.delete(targetMeeting);
        }
    }

    //초대 현황 조회 메서드
    public List<GetParticipantsResponse> getParticipants(Long meetingId) {

        List<GetParticipantsResponse> participantsResponses = new ArrayList<>();
        //해당 meetingId 유효한지 검사
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting);
        if(targetMeeting.isFinished()) {
            throw new IllegalArgumentException("해당 모임은 종료되었습니다!");
        }
        //해당 모임으로 조회되는 모든 초대 조회
        List<Participant> participants = participantRepository.findAllByMeeting(targetMeeting);
        //초대에서 필요한 정보만 추출
        participants.forEach(participant -> {
            Long fromId = participant.getFromId();
            Long toId = participant.getToId();
            User toUser = findService.valid(userRepository.findById(toId), EntityType.User);
            participantsResponses.add(GetParticipantsResponse.builder()
                            .fromId(fromId)
                            .fromName(findService.valid(userRepository.findById(fromId), EntityType.User).getName())
                            .toId(toId)
                            .toName(toUser.getName())
                            .status(participant.isStatus())
                            .toImage(toUser.getProfileImage())
                            .build());
        });
        return participantsResponses;
    }

    //해당 유저가 참여중인 모임 조회 매서드
    public List<GetMeetingsResponse> getMeetings(Long userId) {

        //해당 userId가 유효한지 검사
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        List<UserMeetingMapping> userMeetingMappings = userMeetingMappingRepository.findByUser(targetUser);
        List<GetMeetingsResponse> meetings = new ArrayList<>();
        userMeetingMappings.forEach(mapping -> {
            Meeting meeting = mapping.getMeeting();
            meetings.add(GetMeetingsResponse.builder()
                    .id(meeting.getId())
                    .title(meeting.getTitle())
                    .description(meeting.getDescription())
                    .link(meeting.getLink())
                    .image(meeting.getImage())
                    .finished(meeting.isFinished())
                    .createdAt(meeting.getCreatedAt())
                    .build());
        });
        return meetings;
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
    public InviteOkResponse inviteOk(Long id) {

        //id 유효한지 검사
        Participant target = findService.valid(participantRepository.findById(id), EntityType.Participant);
        User acceptUser = findService.valid(userRepository.findById(target.getToId()), EntityType.User);
        Meeting targetMeeting = target.getMeeting();
        if(targetMeeting.isFinished()) {
            throw new IllegalArgumentException("해당 모임은 종료되었습니다!");
        }
        //초대상태 변경
        participantRepository.save(Participant.builder()
                .id(target.getId())
                .meeting(targetMeeting)
                .fromId(target.getFromId())
                .toId(target.getToId())
                .status(true)
                .build());

        userMeetingMappingRepository.findAllByMeeting(targetMeeting).forEach(userMeetingMapping -> {
            //해당 모임 구성원에게 추가된 구성원정보 전송
            try {
               fcmService.sendMessageToken(userMeetingMapping.getUser().getId(), null, null, SendAddMemberDto.builder()
                       .meetingId(targetMeeting.getId())
                       .userId(acceptUser.getId())
                       .userName(acceptUser.getName())
                       .userImage(acceptUser.getProfileImage())
                       .build(), MethodType.MeetingAccept, SendType.Data);
            } catch (IOException e) {
               throw new IllegalArgumentException("실시간 데이터전송 실패! : " + e.getMessage());
            }
            //친구등록되어있는 사람들은 모임에 id추가
            Optional<Friend> friend = friendRepository.findByUserSet(acceptUser.getId(), userMeetingMapping.getUser().getId());
            if(friend.isPresent()) {
                List<Long> meets = friend.get().getMeets();
                meets.add(targetMeeting.getId());
                friendRepository.save(Friend.builder()
                        .id(friend.get().getId())
                        .u1(friend.get().getU1())
                        .u2(friend.get().getU2())
                        .u1Bookmark(friend.get().isU1Bookmark())
                        .u2Bookmark(friend.get().isU2Bookmark())
                        .meets(meets)
                        .build());
            } else {
                //이전에 서로 친구가 아닌 경우이므로 친구 새로추가
                List<Long> meets = new ArrayList<>();
                meets.add(targetMeeting.getId());
                friendRepository.save(Friend.builder()
                        .u1(acceptUser.getId())
                        .u2(userMeetingMapping.getUser().getId())
                        .u1Bookmark(false)
                        .u2Bookmark(false)
                        .meets(meets)
                        .build());
                //새로운 친구이므로 상대방에게 푸시알림 전송
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),"친구 추가", acceptUser.getNickName() + "님이 " + userMeetingMapping.getUser().getNickName() + "님을 친구로 추가하셨어요.", null, null, SendType.Notification);
                } catch (IOException e) {
                    throw new IllegalArgumentException("푸시알림 전송실패! : " + e.getMessage());
                }
            }
        });
        //해당 모임에 구성원 추가
        userMeetingMappingRepository.save(UserMeetingMapping.builder()
                .user(acceptUser)
                .meeting(targetMeeting)
                .build());
        return InviteOkResponse.builder()
                .id(targetMeeting.getId())
                .title(targetMeeting.getTitle())
                .description(targetMeeting.getDescription())
                .link(targetMeeting.getLink())
                .image(targetMeeting.getImage())
                .finished(targetMeeting.isFinished())
                .createdAt(targetMeeting.getCreatedAt())
                .build();
    }

    //링크초대수락메서드
    public InviteOkResponse inviteOkLink(Long userId, String link) {

        //id 유효한지 검사
        User acceptUser = findService.valid(userRepository.findById(userId), EntityType.User);
        //모임초대링크로 모임조회
        Meeting targetMeeting = findService.valid(meetingRepository.findByLink(link),EntityType.Meeting);
        if(targetMeeting.isFinished()) {
            throw new IllegalArgumentException("해당 모임은 종료되었습니다!");
        }
        userMeetingMappingRepository.findAllByMeeting(targetMeeting).forEach(userMeetingMapping -> {
            //해당 모임 구성원에게 추가된 구성원정보 전송
            try {
                fcmService.sendMessageToken(userMeetingMapping.getUser().getId(), null, null, SendAddMemberDto.builder()
                        .meetingId(targetMeeting.getId())
                        .userId(acceptUser.getId())
                        .userName(acceptUser.getName())
                        .userImage(acceptUser.getProfileImage())
                        .build(), MethodType.MeetingAccept, SendType.Data);
            } catch (IOException e) {
                throw new IllegalArgumentException("실시간 데이터전송 실패! : " + e.getMessage());
            }
            //친구등록되어있는 사람들은 모임에 id추가
            Optional<Friend> friend = friendRepository.findByUserSet(acceptUser.getId(), userMeetingMapping.getUser().getId());
            if(friend.isPresent()) {
                List<Long> meets = friend.get().getMeets();
                meets.add(targetMeeting.getId());
                friendRepository.save(Friend.builder()
                        .id(friend.get().getId())
                        .u1(friend.get().getU1())
                        .u2(friend.get().getU2())
                        .u1Bookmark(friend.get().isU1Bookmark())
                        .u2Bookmark(friend.get().isU2Bookmark())
                        .meets(meets)
                        .build());
            } else {
                //이전에 서로 친구가 아닌 경우이므로 친구 새로추가
                List<Long> meets = new ArrayList<>();
                meets.add(targetMeeting.getId());
                friendRepository.save(Friend.builder()
                        .u1(acceptUser.getId())
                        .u2(userMeetingMapping.getUser().getId())
                        .u1Bookmark(false)
                        .u2Bookmark(false)
                        .meets(meets)
                        .build());
                //새로운 친구이므로 상대방에게 푸시알림 전송
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),"친구 추가", acceptUser.getName() + "님이 " + userMeetingMapping.getUser().getName() + "님을 친구로 추가하셨어요.", null, null, SendType.Notification);
                } catch (IOException e) {
                    throw new IllegalArgumentException("푸시알림 전송실패! : " + e.getMessage());
                }
            }
        });
        //해당 모임에 구성원 추가
        userMeetingMappingRepository.save(UserMeetingMapping.builder()
                .user(acceptUser)
                .meeting(targetMeeting)
                .build());
        return InviteOkResponse.builder()
                .id(targetMeeting.getId())
                .title(targetMeeting.getTitle())
                .description(targetMeeting.getDescription())
                .link(targetMeeting.getLink())
                .image(targetMeeting.getImage())
                .finished(targetMeeting.isFinished())
                .createdAt(targetMeeting.getCreatedAt())
                .build();
    }
}
