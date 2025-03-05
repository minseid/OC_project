package com.example.OC.service;

import com.example.OC.entity.Meeting;
import com.example.OC.network.fcm.FCMMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
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

    public void sendNotificationToken(String targetToken, String title, String body) throws IOException {
        String message = makeNotification(targetToken,title,body);

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
                                .image(null)
                                .build())
                        .build())
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

    public String makeData(String targetToken, String title, String body, Object dataObject) throws JsonProcessingException {

        Map<String, String> dataMap = objectMapper.convertValue(dataObject,Map.class);

        FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                .message(FCMMessageDto.Message.builder()
                        .token(targetToken)
                        .data(dataMap)
                        .build())
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

}
