package com.example.OC.controller;

import com.example.OC.entity.Schedule;
import com.example.OC.network.request.AddScheduleRequest;
import com.example.OC.network.request.EditScheduleRequest;
import com.example.OC.service.ScheduleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/api/schedule/add")
    public ResponseEntity<Schedule> addSchedule(@RequestBody AddScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.addSchedule(request.getMeetingId(), request.getDate(), request.getTime()));
    }

    @GetMapping("/api/schedule/{id}")
    public ResponseEntity<Schedule> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @PutMapping("/api/schedule/edit")
    public ResponseEntity<Schedule> editSchedule(@RequestBody EditScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.editSchedule(request.getScheduleId(), request.getDate(), request.getTime()));
    }

    @DeleteMapping("/api/schedule/delete/{id}")
    public ResponseEntity<Schedule> deleteSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.deleteSchedule(id));
    }

}
