package com.example.OC.controller;

import com.example.OC.constant.ExceptionManager;
import com.example.OC.entity.Comment;
import com.example.OC.entity.Place;
import com.example.OC.network.request.*;
import com.example.OC.network.response.GetCommentResponse;
import com.example.OC.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController extends ExceptionManager {

    private final ModelMapper modelMapper;
    private final PlaceService placeService;

    @PostMapping("/api/place/add")
    public ResponseEntity<Place> addPlace(@RequestBody AddPlaceRequest request) {
        Place saved = placeService.addPlace(request.getMeetingId(), request.getUserid(), request.getName(), request.getAddress());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/api/place/delete")
    public ResponseEntity<Place> deletePlace(@RequestBody CommonPlaceRequest request) {
        Place target = placeService.deletePlace(request.getPlaceId());
        return ResponseEntity.ok(target);
    }

    @PostMapping("/api/place/pick")
    public ResponseEntity<Place> pickPlace(@RequestBody CommonPlaceRequest request) {
        Place target = placeService.pickPlace(request.getPlaceId(), request.getMeetingId());
        return ResponseEntity.ok(target);
    }

    @PostMapping("/api/place/comment/add")
    public ResponseEntity<Comment> addComment(@RequestBody AddCommentRequest request) {
        Comment saved = placeService.addComment(request.getPlaceId(), request.getUserId(), request.getDescription());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/api/place/comment/edit")
    public ResponseEntity<Comment> editComment(@RequestBody EditCommentRequest request) {
        Comment edited = placeService.editComment(request.getCommentId(), request.getUserId(), request.getDescription());
        return ResponseEntity.ok(edited);
    }

    @DeleteMapping("/api/place/comment/delete")
    public ResponseEntity<Comment> deleteComment(@RequestBody DeleteCommentRequest request) {
        Comment deleted = placeService.deleteComment(request.getCommentId(), request.getUserId());
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/api/place/comment/{id}")
    public ResponseEntity<List<GetCommentResponse>> getComments(@PathVariable Long id) {
        List<GetCommentResponse> comments = placeService.getComment(id);
        return ResponseEntity.ok(comments);
    }

}
