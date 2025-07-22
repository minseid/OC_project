package com.where.service;

import com.where.constant.EntityType;
import com.where.constant.NoticeStatus;
import com.where.entity.Inquiry;
import com.where.entity.Notice;
import com.where.network.request.AddAnswerRequest;
import com.where.network.request.AddInquiryRequest;
import com.where.network.request.AddNoticeRequest;
import com.where.network.request.EditNoticeRequest;
import com.where.network.response.*;
import com.where.repository.InquiryRepository;
import com.where.repository.NoticeRepository;
import com.where.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final NoticeRepository noticeRepository;
    private final InquiryRepository inquiryRepository;
    private final AwsS3Service awsS3Service;
    private final FCMService fcmService;
    private final FindService findService;
    private final UserRepository userRepository;

    //유저가 1:1문의를 조회하는 메서드
    public List<GetInquiryResponse> getInquiry(Long userId) {

        //userId 유효성 검사후 해당유저가 등록한 모든 1:1조회
        List<GetInquiryResponse> inquiryList = new ArrayList<>();
        inquiryRepository.findAllByUser(findService.valid(userRepository.findById(userId), EntityType.User)).forEach(inquiry -> {
            inquiryList.add(GetInquiryResponse.builder()
                    .id(inquiry.getId())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .images(inquiry.getImages())
                    .answered(inquiry.isAnswered())
                    .answerContent(inquiry.getAnswerContent())
                    .inquiryDate(inquiry.getCreatedAt().toLocalDate())
                    .answerDate(inquiry.getUpdatedAt().toLocalDate())
                    .build());
        });
        return inquiryList;
    }

    //유저가 1:1문의를 등록하는 메서드
    public AddInquiryResponse addInquiry(AddInquiryRequest request, List<MultipartFile> images) {

        //이미지가 여러개일수 있으므로 List로 작성
        List<String> imageList = new ArrayList<>();
        //이미지저장할때 InquiryId가 필요하므로 DB에 나머지내용 먼저 저장
        Inquiry saved = inquiryRepository.save(Inquiry.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(findService.valid(userRepository.findById(request.getUserId()),EntityType.User))
                .answered(false)
                .build());
        LocalDate date = saved.getCreatedAt().toLocalDate();
        final Long inquiryId = saved.getId();
        log.error(images.toString());
        if(images != null && !images.isEmpty()) {
            images.forEach(image -> {
                log.warn(image.getOriginalFilename());
                if(image != null && !image.isEmpty() && image.getOriginalFilename() != null && image.getOriginalFilename().length() > 0) {
                    imageList.add(awsS3Service.saveInquiryImage(image, inquiryId));
                }
            });
        }
        Inquiry savedLast = inquiryRepository.save(Inquiry.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .user(saved.getUser())
                .images(imageList)
                .answered(saved.isAnswered())
                .answerContent(saved.getAnswerContent())
                .build());
        return AddInquiryResponse.builder()
                .id(savedLast.getId())
                .title(savedLast.getTitle())
                .content(savedLast.getContent())
                .images(savedLast.getImages())
                .answered(savedLast.isAnswered())
                .answerContent(savedLast.getAnswerContent())
                .inquiryDate(date)
                .build();
    }

    //관리자가 1:1문의 조회하는 메서드
    public List<GetInquiryAdminResponse> getInquiryAdmin(int type) {
        List<GetInquiryAdminResponse> responseList = new ArrayList<>();
        List<Inquiry> inquiries = new ArrayList<>();
        switch (type) {
            case 1: //답변있는것만 조회
                inquiries = inquiryRepository.findAllByAnswered(true);
                break;
            case 2: //답변없는것만 조회
                inquiries = inquiryRepository.findAllByAnswered(false);
                break;
            case 3: //모두 조회
                inquiries = inquiryRepository.findAll();
                break;
            default:
                throw new IllegalArgumentException("유형이 올바르지 않습니다!");
        }
        inquiries.forEach(inquiry -> {
            responseList.add(GetInquiryAdminResponse.builder()
                    .id(inquiry.getId())
                    .userId(inquiry.getUser().getId())
                    .userName(inquiry.getUser().getNickName())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .images(inquiry.getImages())
                    .answered(inquiry.isAnswered())
                    .answerContent(inquiry.getAnswerContent())
                    .inquiryDate(inquiry.getCreatedAt().toLocalDate())
                    .answerDate(inquiry.getUpdatedAt().toLocalDate())
                    .build());
        });
        return responseList;
    }

    //관리자가 1:1문의에 답변다는 메서드
    public AddInquiryAdminResponse answerInquiry(AddAnswerRequest request) {
        Inquiry target = findService.valid(inquiryRepository.findById(request.getId()),EntityType.Inquiry);
        LocalDate date = target.getCreatedAt().toLocalDate();
        Inquiry saved = inquiryRepository.save(Inquiry.builder()
                .id(target.getId())
                .user(target.getUser())
                .title(target.getTitle())
                .content(target.getContent())
                .images(target.getImages())
                .answered(true)
                .answerContent(request.getAnswerContent())
                .build());
        saved = findService.valid(inquiryRepository.findById(request.getId()),EntityType.Inquiry);
        LocalDate updatedAt = LocalDate.now();
        return AddInquiryAdminResponse.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .userName(saved.getUser().getName())
                .title(saved.getTitle())
                .content(saved.getContent())
                .images(saved.getImages())
                .answered(saved.isAnswered())
                .answerContent(saved.getAnswerContent())
                .inquiryDate(date)
                .answerDate(updatedAt)
                .build();
    }

    //공지 조회하는 메서드
    public List<GetNoticeResponse> getNotice() {
        List<GetNoticeResponse> responseList = new ArrayList<>();
        noticeRepository.findAllByNoticeStatus(NoticeStatus.Notice).forEach(notice -> {
            responseList.add(GetNoticeResponse.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .date(notice.getCreatedAt().toLocalDate())
                    .build());
        });
        return responseList;
    }

    //공지 등록하는 메서드
    public AddNoticeResponse addNotice(AddNoticeRequest request) {
        Notice saved = noticeRepository.save(Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeStatus(NoticeStatus.Notice)
                .build());
        return AddNoticeResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .date(saved.getCreatedAt().toLocalDate())
                .build();
    }

    //공지 삭제하는 메서드
    public void deleteNotice(Long id) {
        //공지 id 유효성 검증
        findService.valid(noticeRepository.findById(id),EntityType.Notice);
        noticeRepository.deleteById(id);
    }

    //공지 수정하는 메서드
    public EditNoticeResponse editNotice(EditNoticeRequest request) {
        Notice target = findService.valid(noticeRepository.findById(request.getId()),EntityType.Notice);
        if((request.getTitle()==null && request.getContent()==null) || (request.getTitle().isBlank() && request.getContent().isBlank()) || (request.getTitle().equals(target.getTitle()) && request.getContent().equals(target.getContent()))){
            throw new IllegalArgumentException("수정사항이 없습니다!");
        }
        Notice saved = noticeRepository.save(Notice.builder()
                .id(target.getId())
                .title(request.getTitle()==null? target.getTitle() : request.getTitle())
                .content(request.getContent()==null? target.getContent() : request.getContent())
                .noticeStatus(target.getNoticeStatus())
                .build());
        return EditNoticeResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }

    //FAQ 조회하는 메서드
    public List<GetNoticeResponse> getFaq() {
        List<GetNoticeResponse> responseList = new ArrayList<>();
        noticeRepository.findAllByNoticeStatus(NoticeStatus.FAQ).forEach(notice -> {
            responseList.add(GetNoticeResponse.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .date(notice.getCreatedAt().toLocalDate())
                    .build());
        });
        return responseList;
    }

    //FAQ 등록하는 메서드
    public AddNoticeResponse addFaq(AddNoticeRequest request) {
        Notice saved = noticeRepository.save(Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticeStatus(NoticeStatus.FAQ)
                .build());
        return AddNoticeResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .date(saved.getCreatedAt().toLocalDate())
                .build();
    }

    //FAQ 삭제하는 메서드
    public void deleteFaq(Long id) {
        //공지 id 유효성 검증
        findService.valid(noticeRepository.findById(id),EntityType.Notice);
        noticeRepository.deleteById(id);
    }

    //FAQ 수정하는 메서드
    public EditNoticeResponse editFaq(EditNoticeRequest request) {
        Notice target = findService.valid(noticeRepository.findById(request.getId()),EntityType.Notice);
        if((request.getTitle()==null && request.getContent()==null) || (request.getTitle().isBlank() && request.getContent().isBlank()) || (request.getTitle().equals(target.getTitle()) && request.getContent().equals(target.getContent()))){
            throw new IllegalArgumentException("수정사항이 없습니다!");
        }
        Notice saved = noticeRepository.save(Notice.builder()
                .id(target.getId())
                .title(request.getTitle()==null? target.getTitle() : request.getTitle())
                .content(request.getContent()==null? target.getContent() : request.getContent())
                .noticeStatus(target.getNoticeStatus())
                .build());
        return EditNoticeResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }
}
