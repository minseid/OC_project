package com.example.OC.controller;

import com.example.OC.entity.Comment;
import com.example.OC.entity.Link;
import com.example.OC.entity.Place;
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
    public ResponseEntity<Void> deletePlace(@RequestBody Long placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/place/pick")
    public ResponseEntity<PickPlaceResponse> pickPlace(@RequestBody Long placeId) {
        return ResponseEntity.ok(placeService.pickPlace(placeId));
    }

    @PostMapping("/api/place/like")
    public ResponseEntity<PickPlaceResponse> likePlace(@RequestBody LikePlaceRequest request) {
        return ResponseEntity.ok(placeService.likePlace(request.getPlaceId(), request.isLike()));
    }

    @PostMapping("/api/place/comment")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(placeService.addComment(request.getPlaceId(), request.getUserId(), request.getDescription()));
    }

    @PutMapping("/api/place/comment")
    public ResponseEntity<EditCommentResponse> editComment(@RequestBody EditCommentRequest request) {
        return ResponseEntity.ok(placeService.editComment(request.getCommentId(), request.getUserId(), request.getDescription()));
    }

    @DeleteMapping("/api/place/comment")
    public ResponseEntity<Void> deleteComment(@RequestBody DeleteCommentRequest request) {
        placeService.deleteComment(request.getCommentId(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/place/comment/{id}")
    public ResponseEntity<List<GetCommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.getComment(id));
    }

}
