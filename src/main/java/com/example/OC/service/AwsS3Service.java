package com.example.OC.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.OC.constant.ImageType;
import com.example.OC.entity.User;
import com.example.OC.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AwsS3Service {
    
    //임시주석
    private final UserRepository userRepository;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final String profileImgDir = "profile/";
    private final String meetingImgDir = "meeting/";
    private final String inquiryImgDir = "inquiry/";

    public String saveMeetingImage(MultipartFile multipartFile, Long meetingId)  {
        return upload(multipartFile, meetingImgDir, meetingId);
    }

    public String saveProfileImage(MultipartFile multipartFile, Long userId)  {
        return upload(multipartFile, profileImgDir, userId);
    }

    public String saveInquiryImage(MultipartFile multipartFile, Long inquiryId)  {
        return upload(multipartFile, inquiryImgDir, inquiryId);
    }

    public String editMeetingImage(MultipartFile multipartFile, Long meetingId, String link) {
        delete(link,ImageType.Meeting);
        return upload(multipartFile, meetingImgDir, meetingId);
    }

    public String editfrofileImage(MultipartFile multipartFile, Long userId, String link) {
        delete(link,ImageType.Profile);
        return upload(multipartFile, inquiryImgDir, userId);
    }

    public String editInquiryImage(MultipartFile multipartFile, Long inquiryId, String link) {
        delete(link,ImageType.Inquiry);
        return upload(multipartFile, inquiryImgDir, inquiryId);
    }

    private String upload(MultipartFile uploadFile, String dir, Long id) {
        File file = new File(uploadFile.getOriginalFilename());
        String fileName;
        try {
            InputStream inputStream = uploadFile.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/"+ uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")));
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            fileName = dir + id + "/" + UUID.randomUUID() + file.getName();
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드 실패! : " + e.getMessage());
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String link, ImageType type) {

        String targetLink;
        String[] splitedLink;
        switch(type){
            case Profile:
                targetLink = link.substring(link.indexOf(profileImgDir));
                splitedLink = targetLink.split("/");
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, splitedLink[0]+"/"+splitedLink[1]+"/"+splitedLink[2].substring(0,36) + URLDecoder.decode(splitedLink[2].substring(36))));
                break;
            case Meeting:
                targetLink = link.substring(link.indexOf(meetingImgDir));
                splitedLink = targetLink.split("/");
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, splitedLink[0]+"/"+splitedLink[1]+"/"+splitedLink[2].substring(0,36) + URLDecoder.decode(splitedLink[2].substring(36))));
                break;
            case Inquiry:
                targetLink = link.substring(link.indexOf(inquiryImgDir));
                splitedLink = targetLink.split("/");
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, splitedLink[0]+"/"+splitedLink[1]+"/"+splitedLink[2].substring(0,36) + URLDecoder.decode(splitedLink[2].substring(36))));
                break;
            default:
                break;
        }
    }
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        // 파일명 설정 (UUID 사용)
        String fileName = "profile-images/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // S3에 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        // S3 URL 반환
        String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

        // DB에 이미지 URL 저장
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        //user.setProfileImage(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }
}
