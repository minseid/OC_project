package com.example.OC.controller;

import com.example.OC.constant.ExceptionManager;
import com.example.OC.entity.Place;
import com.example.OC.network.request.AddPlaceRequest;
import com.example.OC.network.request.CommonPlaceRequest;
import com.example.OC.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        Place target = placeService.deletePlace(request.getPlaceId(), request.getMeetingId());
        return ResponseEntity.ok(target);
    }

    @PostMapping("/api/place/pick")
    public ResponseEntity<Place> pickPlace(@RequestBody CommonPlaceRequest request) {
        Place target = placeService.pickPlace(request.getPlaceId(), request.getMeetingId());
        return ResponseEntity.ok(target);
    }


}
