package com.example.OC.controller;

import com.example.OC.network.request.AddInquiryRequest;
import com.example.OC.network.response.AddInquiryResponse;
import com.example.OC.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/api/inquiry")
    public ResponseEntity<AddInquiryResponse> addInquiry(@RequestPart AddInquiryRequest request, @RequestPart List<MultipartFile> images) {

    }
}
