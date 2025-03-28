package com.example.OC.controller;

import com.example.OC.network.request.*;
import com.example.OC.network.response.*;
import com.example.OC.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController{

    private final ModelMapper modelMapper;
    private final PlaceService placeService;

    @PostMapping("/api/place")
    public ResponseEntity<AddPlaceResponse> addPlace(@RequestBody AddPlaceRequest request) {
        return ResponseEntity.ok(placeService.addPlace(request.getMeetingId(), request.getUserid(), request.getName(), request.getAddress(), request.getNaverLink()));
    }

    @DeleteMapping("/api/place")
    public ResponseEntity<Void> deletePlace(@RequestBody Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/place/pick")
    public ResponseEntity<PickPlaceResponse> pickPlace(@RequestBody Long id) {
        return ResponseEntity.ok(placeService.pickPlace(id));
    }

    @PostMapping("/api/place/like")
    public ResponseEntity<PickPlaceResponse> likePlace(@RequestBody LikePlaceRequest request) {
        return ResponseEntity.ok(placeService.likePlace(request.getId(), request.isLike()));
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

    @GetMapping("/api/place/comment/{id}")
    public ResponseEntity<List<GetCommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.getComment(id));
    }

    @GetMapping("/api/place")
    public ResponseEntity<List<GetPlaceResponse>> getPlaces(@RequestParam Long meetingId, @RequestParam Long userId) {
        return ResponseEntity.ok(placeService.getplaces(meetingId, userId));
    }

}