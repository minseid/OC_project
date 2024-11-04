package com.example.OC.service;

import com.example.OC.repository.MeetingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;

    @Transactional
    public MeetingResponse createMeeting(userDetails,meetingRequest){
        Meeting meeting = Meeting.builder()
                .meetingName(meetingRequest.getMeetingName())
                .meetingDescription(meetingRequest.getMeetingDescription())
                .meetingDate(meetingRequest.getMeetingDate())
                .meetingTime(meetingRequest.getMeetingTime())
                .meetingDuration(meetingRequest.getMeetingDuration())
                .meetingPlace(meetingRequest.getMeetingPlace())
                .build();
    }
    return MeetingResponse.from(Meeting)
}
