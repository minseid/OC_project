package com.example.OC.controller;

import com.example.OC.entity.Meeting;
import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/api/meeting/add")
    public ResponseEntity<AddMeetingResponse> addMeeting(AddMeetingRequest request) {
        Meeting saved = meetingService.addMeeting(request.getTitle(), request.getDescription(), request.getImage(), request.getFromId(),request.getParticipants());

        return ResponseEntity.ok(AddMeetingResponse.builder()
                        .id(saved.getId())
                        .title(saved.getTitle())
                        .description(saved.getDescription())
                        .link(saved.getLink())
                        .image(saved.getImage())
                        .build());
    }
}
