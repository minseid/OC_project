package com.where.controller;

import com.where.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @GetMapping("/api/version")
    public ResponseEntity<Boolean> checkVersion(@RequestParam String type, @RequestParam String version) {
        return ResponseEntity.ok(versionService.checkVersion(type, version));
    }

}
