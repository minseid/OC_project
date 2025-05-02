package com.where.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.where.dto.VersionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class VersionService {

    public Boolean checkVersion(String type, String version) {
        String homeDirectory = System.getProperty("user.home");
        String filePath = "/home/ubuntu/version.json"; // 읽을 JSON 파일 경로

        ObjectMapper objectMapper = new ObjectMapper();
        VersionDto versionDto;
        try {
            versionDto = objectMapper.readValue(new File(filePath), VersionDto.class);
            switch (type) {
                case "apple":
                    return versionDto.getApple().equals(version);
                case "android":
                    return versionDto.getAndroid().equals(version);
                default:
                    throw new IllegalArgumentException("타입을 올바르게 입력해주세요");
            }
        } catch (IOException e) {
            log.error("홈 디렉토리 JSON 파일 읽기 오류 (" + filePath + "): " + e.getMessage());
            throw new IllegalArgumentException("파일읽기오류!");
        }
    }
}
