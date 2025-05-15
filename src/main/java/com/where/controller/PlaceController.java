package com.where.controller;

import com.where.network.request.*;
import com.where.network.response.*;
import com.where.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController{

    private final PlaceService placeService;

    @PostMapping("/api/place")
    public ResponseEntity<AddPlaceResponse> addPlace(@RequestBody AddPlaceRequest request) {
        return ResponseEntity.ok(placeService.addPlace(request.getMeetingId(), request.getUserId(), request.getName(), request.getAddress()));
    }

    @DeleteMapping("/api/place")
    public ResponseEntity<Void> deletePlace(@RequestBody CommonIdAndUserRequest request) {
        placeService.deletePlace(request.getId(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/place/pick")
    public ResponseEntity<PickPlaceResponse> pickPlace(@RequestBody CommonIdAndUserRequest request) {
        return ResponseEntity.ok(placeService.pickPlace(request.getId(), request.getUserId()));
    }

    @PostMapping("/api/place/like")
    public ResponseEntity<PickPlaceResponse> likePlace(@RequestBody LikePlaceRequest request) {
        return ResponseEntity.ok(placeService.likePlace(request.getId(), request.getUserId()));
    }

    @PostMapping("/api/place/comment")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(placeService.addComment(request.getPlaceId(), request.getUserId(), request.getDescription()));
    }

    @PutMapping("/api/place/comment")
    public ResponseEntity<EditCommentResponse> editComment(@RequestBody EditCommentRequest request) {
        return ResponseEntity.ok(placeService.editComment(request.getId(), request.getUserId(), request.getDescription()));
    }

    @DeleteMapping("/api/place/comment")
    public ResponseEntity<Void> deleteComment(@RequestBody DeleteCommentRequest request) {
        placeService.deleteComment(request.getId(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/place/comment")
    public ResponseEntity<List<GetCommentResponse>> getComments(@RequestParam Long placeId, @RequestParam Long userId) {
        return ResponseEntity.ok(placeService.getComment(placeId, userId));
    }

    @GetMapping("/api/place")
    public ResponseEntity<List<GetPlaceResponse>> getPlaces(@RequestParam Long meetingId, @RequestParam Long userId) {
        return ResponseEntity.ok(placeService.getPlaces(meetingId, userId));
    }

}