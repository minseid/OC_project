package com.example.OC.controller;

import com.example.OC.entity.Schedule;
import com.example.OC.network.request.AddScheduleRequest;
import com.example.OC.network.request.EditScheduleRequest;
import com.example.OC.network.response.AddScheduleResponse;
import com.example.OC.network.response.EditScheduleResponse;
import com.example.OC.network.response.GetScheduleResponse;
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
    public ResponseEntity<AddScheduleResponse> addSchedule(@RequestBody AddScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.addSchedule(request.getMeetingId(), request.getDate(), request.getTime()));
    }

    @GetMapping("/api/schedule/{id}")
    public ResponseEntity<GetScheduleResponse> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @PutMapping("/api/schedule/edit")
    public ResponseEntity<EditScheduleResponse> editSchedule(@RequestBody EditScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.editSchedule(request.getMeetingId(), request.getDate(), request.getTime()));
    }

    @DeleteMapping("/api/schedule/delete")
    public ResponseEntity<Void> deleteSchedule(@RequestBody Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

}
