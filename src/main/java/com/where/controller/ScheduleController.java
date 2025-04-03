package com.where.controller;

import com.where.network.request.AddScheduleRequest;
import com.where.network.request.DeleteScheduleRequest;
import com.where.network.request.EditScheduleRequest;
import com.where.network.response.AddScheduleResponse;
import com.where.network.response.EditScheduleResponse;
import com.where.network.response.GetScheduleResponse;
import com.where.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/api/schedule")
    public ResponseEntity<AddScheduleResponse> addSchedule(@RequestBody AddScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.addSchedule(request.getMeetingId(), request.getDate(), request.getTime(), request.getUserId()));
    }

    @GetMapping("/api/schedule/{id}")
    public ResponseEntity<GetScheduleResponse> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @PutMapping("/api/schedule")
    public ResponseEntity<EditScheduleResponse> editSchedule(@RequestBody EditScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.editSchedule(request.getMeetingId(), request.getDate(), request.getTime(), request.getUserId()));
    }

    @DeleteMapping("/api/schedule")
    public ResponseEntity<Void> deleteSchedule(@RequestBody DeleteScheduleRequest request) {
        scheduleService.deleteSchedule(request.getMeetingId(), request.getUserId());
        return ResponseEntity.ok().build();
    }

}
