package com.example.OC.controller;

import com.example.OC.entity.Meeting;
import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.request.EditMeetingRequest;
import com.example.OC.network.request.QuitMeetingRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.network.response.EditMeetingResponse;
import com.example.OC.network.response.QuitMeetingResponse;
import com.example.OC.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final ModelMapper modelMapper;

    @PostMapping("/api/meeting/add")
    public ResponseEntity<AddMeetingResponse> addMeeting(AddMeetingRequest request) {
        Meeting saved = meetingService.addMeeting(request.getTitle(), request.getDescription(), request.getImage(), request.getFromId(),request.getParticipants());
        //여기에 초대관련 넣거나 서비스단에 초대를 넣어야될듯
        return ResponseEntity.ok(modelMapper.map(saved, AddMeetingResponse.class));
    }

    @PutMapping("/api/meeting/edit")
    public ResponseEntity<EditMeetingResponse> editMeeting(EditMeetingRequest request) {
        Meeting updated = meetingService.editMeeting(request.getId(),request.getTitle(),request.getDescription(),request.getImage(), request.getFromId(), request.getParticipants());
        return ResponseEntity.ok(modelMapper.map(updated, EditMeetingResponse.class));
    }

    @DeleteMapping("/api/meeting/quit")
    public ResponseEntity<QuitMeetingResponse> quitMeeting(QuitMeetingRequest request) {
        Meeting deleted = meetingService.quitMeeting(request.getUserId(), request.getId());
    }
}
