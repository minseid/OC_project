package com.example.OC.controller;

import com.example.OC.constant.ExceptionManager;
import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.request.EditMeetingRequest;
import com.example.OC.network.request.InviteRequest;
import com.example.OC.network.request.QuitMeetingRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.network.response.EditMeetingResponse;
import com.example.OC.network.response.QuitMeetingResponse;
import com.example.OC.repository.MeetingRepository;
import com.example.OC.repository.ParticipantRepository;
import com.example.OC.service.FCMService;
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
    private final FCMService fcmService;


    @PostMapping("/api/meeting/add")
    public ResponseEntity<AddMeetingResponse> addMeeting(@RequestBody AddMeetingRequest request) {
        Meeting saved = meetingService.addMeeting(request.getTitle(), request.getDescription(), request.getImage(), request.getFromId(),request.getParticipants());
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

    @GetMapping("/api/meeting/participant/{id}")
    public ResponseEntity<List<Participant>> getParticipants(@PathVariable("id") Long meetingId) {
        List<Participant> participants = meetingService.loadParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/api/meeting/{id}")
    public ResponseEntity<List<Meeting>> getMeetings(@PathVariable("id") Long userId) {
        List<Meeting> meetings = meetingService.getMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/api/meeting/invite")
    public ResponseEntity<Participant> inviteMeeting(@RequestBody InviteRequest request) {
        Participant saved = meetingService.addParticipant(request.getMeetingId(), request.getFromId(), request.getToId());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/api/meeting/invite/ok/{id}")
    public ResponseEntity<Meeting> inviteOk(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.inviteOk(id));
    }


}
