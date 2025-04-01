package com.where.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/.well-known")
public class DeepLinkController {

    @GetMapping("/apple/apple-app-site-association")
    public ResponseEntity<Resource> getAppleAppSiteAssociation() {
        Resource resource = new ClassPathResource("static/wellknown/apple-app-site-association");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }

    @GetMapping("/assetlinks.json")
    public ResponseEntity<Resource> getAssetlinks() {
        Resource resource = new ClassPathResource("static/wellknown/assetlinks.json");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resource);
    }
}
