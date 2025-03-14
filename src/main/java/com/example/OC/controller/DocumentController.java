package com.example.OC.controller;

import com.example.OC.network.request.AddAnswerRequest;
import com.example.OC.network.request.AddInquiryRequest;
import com.example.OC.network.request.AddNoticeRequest;
import com.example.OC.network.request.EditNoticeRequest;
import com.example.OC.network.response.*;
import com.example.OC.service.DocumentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/api/inquiry/{id}")
    public ResponseEntity<List<GetInquiryResponse>> getInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getInquiry(id));
    }

    @PostMapping("/api/inquiry")
    public ResponseEntity<AddInquiryResponse> addInquiry(@RequestPart("data") AddInquiryRequest request, @RequestPart List<MultipartFile> file) {
        return ResponseEntity.ok(documentService.addInquiry(request, file));
    }

    @GetMapping("/api/admin/inquiry/{type}")
    public ResponseEntity<List<GetInquiryAdminResponse>> getInquiry(@PathVariable int type) {
        return ResponseEntity.ok(documentService.getInquiryAdmin(type));
    }

    @PostMapping("/api/admin/inquiry")
    public ResponseEntity<AddInquiryAdminResponse> answerInquiry(@RequestBody AddAnswerRequest request) {
        return ResponseEntity.ok(documentService.answerInquiry(request));
    }

    @GetMapping("/api/notice")
    public ResponseEntity<List<GetNoticeResponse>> getNotice() {
        return ResponseEntity.ok(documentService.getNotice());
    }

    @PostMapping("/api/admin/notice")
    public ResponseEntity<AddNoticeResponse> addNotice(@RequestBody AddNoticeRequest request) {
        return ResponseEntity.ok(documentService.addNotice(request));
    }

    @DeleteMapping("/api/admin/notice")
    public ResponseEntity<Void> deleteNotice(@RequestBody Long id) {
        documentService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/admin/notice")
    public ResponseEntity<EditNoticeResponse> editNotice(@RequestBody EditNoticeRequest request) {
        return ResponseEntity.ok(documentService.editNotice(request));
    }

    @GetMapping("/api/FAQ")
    public ResponseEntity<List<GetNoticeResponse>> getFaq() {
        return ResponseEntity.ok(documentService.getFaq());
    }

    @PostMapping("/api/admin/FAQ")
    public ResponseEntity<AddNoticeResponse> addFaq(@RequestBody AddNoticeRequest request) {
        return ResponseEntity.ok(documentService.addFaq(request));
    }

    @PutMapping("/api/admin/FAQ")
    public ResponseEntity<EditNoticeResponse> editFaq(@RequestBody EditNoticeRequest request) {
        return ResponseEntity.ok(documentService.editFaq(request));
    }

    @DeleteMapping("/api/admin/FAQ")
    public ResponseEntity<Void> deleteFaq(@RequestBody Long id) {
        documentService.deleteFaq(id);
        return ResponseEntity.ok().build();
    }
}
