package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.constant.MethodType;
import com.example.OC.constant.SendType;
import com.example.OC.entity.Meeting;
import com.example.OC.network.fcm.FCMMessageDto;
import com.example.OC.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//서비스 구현 완료 근데 전송내용은 결정해서 수정해야됨
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FCMService {


    //fcm으로 전송을 위한 accesstoken발급
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/audi-8284f/messages:send";
    private final ObjectMapper objectMapper;
    private final FindService findService;
    private final UserRepository userRepository;

    public void sendMessageToken(Long userId, String title, String body, Object dataObject, MethodType methodType, SendType sendType) throws IOException {
        String targetToken = findService.valid(userRepository.findById(userId), EntityType.User).getFcmKey();
        String message = switch (sendType) {
            case Data -> makeData(targetToken, dataObject, methodType);
            case Notification -> makeNotification(targetToken, title, body);
            default -> makeNotiAndData(targetToken, title, body, dataObject,methodType);
        };
        log.warn(message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + getAccessToken())
                .build();

        //동기처리시 execute사용, 비동기시 enqueue사용
        Response response = client.newCall(request)
                .execute();

        System.out.println(response.body().string());
    }

    public String makeNotification(String targetToken, String title, String body) throws JsonProcessingException {

        FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                .message(FCMMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FCMMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .build())
                        .build())
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

    public String makeData(String targetToken, Object dataObject, MethodType methodType) throws JsonProcessingException {

        Map<String, Object> dataMap = objectMapper.convertValue(dataObject,Map.class);
        dataMap.replaceAll((key, value) -> String.valueOf(value));
        Map<String, Object> wrappedMap = new HashMap<>();
        wrappedMap.put("code", String.valueOf(methodType.getCode()));
        wrappedMap.putAll(dataMap);
        FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                .message(FCMMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(null)
                        .data(wrappedMap)
                        .build())
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

    public String makeNotiAndData(String targetToken, String title, String body, Object dataObject, MethodType methodType) throws JsonProcessingException {

        Map<String, Object> dataMap = objectMapper.convertValue(dataObject,Map.class);
        dataMap.replaceAll((key, value) -> String.valueOf(value));
        Map<String, Object> wrappedMap = new HashMap<>();
        wrappedMap.put("code", methodType.getCode());
        wrappedMap.put("data", dataMap);

        FCMMessageDto fcmMessageDto= FCMMessageDto.builder()
                .message(FCMMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FCMMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .build())
                        .data(wrappedMap)
                        .build())
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

}
