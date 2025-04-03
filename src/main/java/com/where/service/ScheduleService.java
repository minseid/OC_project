package com.where.service;

import com.where.constant.EntityType;
import com.where.constant.MethodType;
import com.where.constant.SendType;
import com.where.entity.Meeting;
import com.where.entity.Schedule;
import com.where.entity.User;
import com.where.network.fcm.SendAddScheduleDto;
import com.where.network.fcm.SendDeleteScheduleDto;
import com.where.network.fcm.SendEditScheduleDto;
import com.where.network.response.AddScheduleResponse;
import com.where.network.response.EditScheduleResponse;
import com.where.network.response.GetScheduleResponse;
import com.where.repository.MeetingRepository;
import com.where.repository.ScheduleRepository;
import com.where.repository.UserMeetingMappingRepository;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {
    
    private final FindService findService;
    private final ScheduleRepository scheduleRepository;
    private final MeetingRepository meetingRepository;
    private final UserMeetingMappingRepository userMeetingMappingRepository;
    private final FCMService fcmService;
    private final UserRepository userRepository;

    //일정등록 메서드
    public AddScheduleResponse addSchedule(Long meetingId, LocalDate date, LocalTime time, Long userId) {

        //id 유효성 검증
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId),EntityType.Meeting);
        if(!scheduleRepository.existsByMeeting(targetMeeting)) {
            throw new IllegalArgumentException("일정이 이미 등록되어있습니다!");
        }
        if(userMeetingMappingRepository.findByUserAndMeeting(targetUser, targetMeeting).isEmpty()) {
            throw new IllegalArgumentException("해당 유저는 이 모임의 구성원이 아닙니다!");
        }
        Schedule saved = scheduleRepository.save(Schedule.builder()
                .meeting(targetMeeting)
                .date(date)
                .time(time)
                .build());
        //모임구성원에게 정보 전송
        userMeetingMappingRepository.findAllByMeeting(targetMeeting).forEach(userMeetingMapping -> {
            if(!userMeetingMapping.getUser().equals(targetUser)) {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(), null, null, SendAddScheduleDto.builder()
                                    .meetingId(saved.getMeeting().getId())
                                    .date(saved.getDate())
                                    .time(saved.getTime())
                                    .build(),
                            MethodType.ScheduleAdd, SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송 실패! : " + e.getMessage());
                }
            }
        });
        return AddScheduleResponse.builder()
                .meetingId(saved.getMeeting().getId())
                .date(saved.getDate())
                .time(saved.getTime())
                .build();
    }

    //일정수정 메서드
    public EditScheduleResponse editSchedule(Long meetingId, LocalDate date, LocalTime time, Long userId) {

        if(date == null && time == null) {
            throw new IllegalArgumentException("일정 수정내용이 없습니다!");
        }
        //id 유효성 검증
        Meeting targetMeeting = findService.valid(meetingRepository.findById(meetingId),EntityType.Meeting);
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        if(userMeetingMappingRepository.findByUserAndMeeting(targetUser, targetMeeting).isEmpty()) {
            throw new IllegalArgumentException("해당 유저는 이모임의 구성원이 아닙니다!");
        }
        Schedule target = findService.valid(scheduleRepository.findByMeeting(targetMeeting),EntityType.Schedule);
        Schedule saved = scheduleRepository.save(Schedule.builder()
                .id(target.getId())
                .meeting(targetMeeting)
                .date(date == null ? target.getDate() : date)
                .time(time == null ? target.getTime() : time)
                .build());
        //모임구성원에게 데이터 전송
        userMeetingMappingRepository.findAllByMeeting(saved.getMeeting()).forEach(userMeetingMapping -> {
            if(!userMeetingMapping.getUser().equals(targetUser)) {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(),null,null, SendEditScheduleDto.builder()
                                    .meetingId(saved.getMeeting().getId())
                                    .date(saved.getDate())
                                    .time(saved.getTime())
                                    .build(),
                            MethodType.ScheduleEdit,SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송실패! : " + e.getMessage());
                }
            }
        });
        return EditScheduleResponse.builder()
                .meetingId(saved.getMeeting().getId())
                .date(saved.getDate())
                .time(saved.getTime())
                .build();
    }

    //일정삭제메서드
    public void deleteSchedule(Long id, Long userId) {
        
        //id 유효성 검증
        User targetUser = findService.valid(userRepository.findById(userId), EntityType.User);
        Schedule target = findService.valid(scheduleRepository.findByMeeting(findService.valid(meetingRepository.findById(id),EntityType.Meeting)), EntityType.Schedule);
        if(!userMeetingMappingRepository.existsByUserAndMeeting(targetUser, target.getMeeting())) {
            throw new IllegalArgumentException("해당 유저는 이 모임의 구성원이 아닙니다!");
        }
        scheduleRepository.delete(target);
        //모임구성원에게 전송
        userMeetingMappingRepository.findAllByMeeting(target.getMeeting()).forEach(userMeetingMapping -> {
            if(!userMeetingMapping.getUser().equals(targetUser)) {
                try {
                    fcmService.sendMessageToken(userMeetingMapping.getUser().getId(), null, null, SendDeleteScheduleDto.builder()
                                    .meetingId(target.getMeeting().getId())
                                    .build(),
                            MethodType.ScheduleDelete, SendType.Data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("실시간 데이터 전송실패! : " + e.getMessage());
                }
            }
        });
    }

    //일정조회
    public GetScheduleResponse getSchedule(Long meetingId) {
        Schedule target = findService.valid(scheduleRepository.findByMeeting(findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting)), EntityType.Schedule);
        return GetScheduleResponse.builder()
                .meetingId(target.getMeeting().getId())
                .date(target.getDate())
                .time(target.getTime())
                .build();
    }
}
