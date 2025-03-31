package com.example.OC.controller;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.network.request.*;
import com.example.OC.network.response.*;
import com.example.OC.service.AwsS3Service;
import com.example.OC.service.FCMService;
import com.example.OC.service.MeetingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingController{

    private final MeetingService meetingService;


    @PostMapping("/api/meeting")
    public ResponseEntity<AddMeetingResponse> addMeeting(@Valid @RequestPart("data") AddMeetingRequest request, @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(meetingService.addMeeting(request.getTitle(), request.getDescription(), image, request.getFromId(),request.getParticipants()));
    }

    @PutMapping("/api/meeting")
    public ResponseEntity<EditMeetingResponse> editMeeting(@Valid @RequestPart("data") EditMeetingRequest request, @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(meetingService.editMeeting(request.getId(),request.getTitle(),request.getDescription(), request.getUserId(), image));
    }

    @PutMapping("/api/meeting/finish")
    public ResponseEntity<Void> finishMeeting(@Valid @RequestBody FinishMeetingRequest request) {
        meetingService.finishMeeting(request.getId(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/meeting")
    public ResponseEntity<QuitMeetingResponse> quitMeeting(@Valid @RequestBody QuitMeetingRequest request) {
        meetingService.quitMeeting(request.getUserId(), request.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/meeting/participant/{id}")
    public ResponseEntity<List<GetParticipantsResponse>> getParticipants(@PathVariable("id") Long meetingId) {
        List<GetParticipantsResponse> participants = meetingService.getParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/api/meeting/{id}")
    public ResponseEntity<List<GetMeetingsResponse>> getMeetings(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(meetingService.getMeetings(userId));
    }

    @PostMapping("/api/meeting/invite")
    public ResponseEntity<Void> inviteMeeting(@Valid @RequestBody InviteRequest request) {
        meetingService.addParticipant(request.getMeetingId(), request.getFromId(), request.getToId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/meeting/invite/ok")
    public ResponseEntity<InviteOkResponse> inviteOk(@Valid @RequestBody InviteOkRequest request) {
        return ResponseEntity.ok(meetingService.inviteOk(request.getId()));
    }
}
