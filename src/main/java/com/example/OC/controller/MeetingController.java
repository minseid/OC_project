package com.example.OC.controller;

import com.example.OC.entity.Meeting;
import com.example.OC.entity.Participant;
import com.example.OC.network.request.AddMeetingRequest;
import com.example.OC.network.request.EditMeetingRequest;
import com.example.OC.network.request.InviteRequest;
import com.example.OC.network.request.QuitMeetingRequest;
import com.example.OC.network.response.AddMeetingResponse;
import com.example.OC.network.response.EditMeetingResponse;
import com.example.OC.network.response.GetParticipantsResponse;
import com.example.OC.network.response.QuitMeetingResponse;
import com.example.OC.service.AwsS3Service;
import com.example.OC.service.FCMService;
import com.example.OC.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingController{

    private final MeetingService meetingService;
    private final ModelMapper modelMapper;
    private final FCMService fcmService;
    private final AwsS3Service awsS3Service;


    @PostMapping("/api/meeting")
    public ResponseEntity<AddMeetingResponse> addMeeting(@RequestPart("data") AddMeetingRequest request, @RequestPart("image") MultipartFile image) {
        Meeting saved = meetingService.addMeeting(request.getTitle(), request.getDescription(), image, request.getFromId(),request.getParticipants());
        return ResponseEntity.ok(modelMapper.map(saved, AddMeetingResponse.class));
    }

    @PutMapping("/api/meeting")
    public ResponseEntity<EditMeetingResponse> editMeeting(@RequestPart("data") EditMeetingRequest request, @RequestPart("image") MultipartFile image) {
        Meeting updated = meetingService.editMeeting(request.getId(),request.getTitle(),request.getDescription(),image);
        return ResponseEntity.ok(modelMapper.map(updated, EditMeetingResponse.class));
    }

    @DeleteMapping("/api/meeting")
    public ResponseEntity<QuitMeetingResponse> quitMeeting(@RequestBody QuitMeetingRequest request) {
        meetingService.quitMeeting(request.getUserId(), request.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/meeting/participant/{id}")
    public ResponseEntity<List<GetParticipantsResponse>> getParticipants(@PathVariable("id") Long meetingId) {
        List<GetParticipantsResponse> participants = meetingService.getParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/api/meeting/{id}")
    public ResponseEntity<List<Meeting>> getMeetings(@PathVariable("id") Long userId) {
        List<Meeting> meetings = meetingService.getMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/api/meeting/invite")
    public ResponseEntity<Void> inviteMeeting(@RequestBody InviteRequest request) {
        meetingService.addParticipant(request.getMeetingId(), request.getFromId(), request.getToId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/meeting/invite/ok")
    public ResponseEntity<Meeting> inviteOk(@RequestBody Long id) {
        return ResponseEntity.ok(meetingService.inviteOk(id));
    }


}
