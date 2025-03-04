package com.example.OC.controller;

import com.example.OC.constant.ExceptionManager;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.request.EditMeetingRequest;
import com.example.OC.network.request.QuitMeetingRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.network.response.EditMeetingResponse;
import com.example.OC.network.response.QuitMeetingResponse;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.ParticipantRepository;
import com.example.OC.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingController extends ExceptionManager {

    private final MeetingService meetingService;
    private final ModelMapper modelMapper;


    @PostMapping("/api/meeting/add")
    public ResponseEntity<AddMeetingResponse> addMeeting(@RequestBody AddMeetingRequest request) {
        Meeting saved = meetingService.addMeeting(request.getTitle(), request.getDescription(), request.getImage(), request.getFromId(),request.getParticipants());
        //여기에 초대관련 넣거나 서비스단에 초대를 넣어야될듯
        return ResponseEntity.ok(modelMapper.map(saved, AddMeetingResponse.class));
    }

    @PutMapping("/api/meeting/edit")
    public ResponseEntity<EditMeetingResponse> editMeeting(@RequestBody EditMeetingRequest request) {
        Meeting updated = meetingService.editMeeting(request.getId(),request.getTitle(),request.getDescription(),request.getImage());
        return ResponseEntity.ok(modelMapper.map(updated, EditMeetingResponse.class));
    }

    @DeleteMapping("/api/meeting/quit")
    public ResponseEntity<QuitMeetingResponse> quitMeeting(@RequestBody QuitMeetingRequest request) {
        Meeting deleted = meetingService.quitMeeting(request.getUserId(), request.getId());
        return ResponseEntity.ok(modelMapper.map(deleted, QuitMeetingResponse.class));
    }

    @GetMapping("/api/meeting/participant")
    public ResponseEntity<List<Participant>> getParticipants(@RequestBody Long meetingId) {
        List<Participant> participants = meetingService.loadParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/api/meeting")
    public ResponseEntity<List<Meeting>> getMeetings(@RequestBody Long userId) {
        List<Meeting> meetings = meetingService.getMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    //@PostMapping("/api/meeting/invite")
    //초대는 서버알림공부후에 작성

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handlerIllegalAccess(IllegalArgumentException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }

}
