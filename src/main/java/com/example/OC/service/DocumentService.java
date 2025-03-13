package com.example.OC.service;

import com.example.OC.repository.InquiryRepository;
import com.example.OC.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService {

    private final NoticeRepository noticeRepository;
    private final InquiryRepository inquiryRepository;
    private final AwsS3Service awsS3Service;

    public
}
