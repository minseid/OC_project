package com.example.OC.service;

import com.example.OC.entity.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Getter
@ToString
@Builder
@Transactional
@Slf4j
public class ScheduleService extends FindService {

    public Schedule addSchedule(Long meetingId, LocalDate date, LocalTime time) {
        return scheduleRepository.save(Schedule.builder()
                .meeting(findMeeting(meetingId))
                .date(date)
                .time(time)
                .build());
    }

    public Schedule editSchedule(Long id, LocalDate date, LocalTime time) {
        Schedule target = findSchedule(id);
        return scheduleRepository.save(Schedule.builder()
                .id(target.getId())
                .meeting(target.getMeeting())
                .date(date)
                .time(time)
                .build());
    }

    public Schedule deleteSchedule(Long id) {
        Schedule target = findSchedule(id);
        scheduleRepository.delete(target);
        return target;
    }

    public Schedule getSchedule(Long meetingId) {
        return findSchedule(meetingId);
    }
}
