package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.entity.Schedule;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.ScheduleRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Schedule addSchedule(Long meetingId, LocalDate date, LocalTime time) {
        return scheduleRepository.save(Schedule.builder()
                .meeting(findService.valid(meetingRepository.findById(meetingId), EntityType.Meeting))
                .date(date)
                .time(time)
                .build());
    }

    public Schedule editSchedule(Long id, LocalDate date, LocalTime time) {
        Schedule target = findService.valid(scheduleRepository.findById(id), EntityType.Schedule);
        return scheduleRepository.save(Schedule.builder()
                .id(target.getId())
                .meeting(target.getMeeting())
                .date(date)
                .time(time)
                .build());
    }

    public Schedule deleteSchedule(Long id) {
        Schedule target = findService.valid(scheduleRepository.findById(id), EntityType.Schedule);
        scheduleRepository.delete(target);
        return target;
    }

    public Schedule getSchedule(Long meetingId) {
        return findService.valid(scheduleRepository.findById(meetingId), EntityType.Schedule);
    }
}
