package com.where.controller;

import com.where.network.request.*;
import com.where.network.response.*;
import com.where.service.MeetingService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> quitMeeting(@Valid @RequestBody QuitMeetingRequest request) {
        meetingService.quitMeeting(request.getUserId(), request.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/meeting/image/{meetingId}")
    public ResponseEntity<Void> deleteMeetingImage(@PathVariable Long meetingId) {
        meetingService.deleteMeetingImage(meetingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/meeting/participant/{id}")
    public ResponseEntity<List<GetParticipantsResponse>> getParticipants(@PathVariable("id") Long meetingId) {
        List<GetParticipantsResponse> participants = meetingService.getParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/api/participant/{id}")
    public ResponseEntity<List<GetUserInviteResponse>> getUserInvite(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(meetingService.getUserInvites(userId));
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

    @PostMapping("/api/meeting/invite/ok/link")
    public ResponseEntity<InviteOkResponse> inviteOkLink(@Valid @RequestBody InviteOkLinkRequest request) {
        return ResponseEntity.ok(meetingService.inviteOkLink(request.getUserId(), request.getLink()));
    }

    @PostMapping("/api/meeting/invite/ok")
    public ResponseEntity<InviteOkResponse> inviteOk(@Valid @RequestBody InviteOkRequest request) {
        return ResponseEntity.ok(meetingService.inviteOk(request.getId()));
    }

    @GetMapping("/api/meeting/invite/{link}")
    public ResponseEntity<GetInviteResponse> getInviteOk(@PathVariable("link") String link) {
        return ResponseEntity.ok(meetingService.getInvite(link));
    }
}
