package com.where.network.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FCMMessageDto {

    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private Notification notification;
        private Map<String, Object> data;
        private String token;
        private Android android;
        private Apns apns;

    }

    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Notification {
        private String title;
        private String body;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Android {
        private String priority;
    }

    // APNs 구조 수정: headers 와 payload 를 포함
    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Apns {
        private Map<String, String> headers;
        private Payload payload; // <-- Payload 필드 추가
    }

    // APNs Payload 내부 클래스 추가
    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payload {
        @JsonProperty("aps") // JSON 필드명을 "aps"로 매핑
        private Aps aps;
    }

    // APNs aps 내부 클래스 추가
    @Builder
    @AllArgsConstructor
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Aps {
        @JsonProperty("content-available") // JSON 필드명을 "content-available"로 매핑
        private int contentAvailable; // int 나 boolean 도 가능 (1 또는 true)
    }

}
