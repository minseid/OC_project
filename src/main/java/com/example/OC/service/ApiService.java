package com.example.OC.service;

import com.example.OC.dto.PlaceAddressDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ApiService {

    @Value("${KAKAO_MAP_ACCESS_KEY}")
    private String accessKey;
    private final int placeRadius = 50;

    public PlaceAddressDto getKakaoMapPlaceId (String placeName, String placeAddress) {

        String coordinateUrl = "https://dapi.kakao.com/v2/local/search/address?" + "query=" + placeAddress;
        String placeIdUrl = "https://dapi.kakao.com/v2/local/search/keyword?" + "query=" + placeName;

        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        JsonNode rootNode;
        headers.set(HttpHeaders.AUTHORIZATION,"KakaoAK " + accessKey);
        HttpEntity<String> entity = new HttpEntity<String>("",headers);
        ResponseEntity<String> response = restTemplate.exchange(coordinateUrl, HttpMethod.GET, entity, String.class);
        try {
            rootNode = objectMapper.readTree(response.getBody().toString());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("kakaoapi 응답변환실패!");
        }
        JsonNode roadAddressNode = rootNode.path("documents").get(0).path("road_address");
        String x = roadAddressNode.path("x").asText();
        String y = roadAddressNode.path("y").asText();
        response = restTemplate.exchange(placeIdUrl + "&x=" + x + "&y=" + y + "&radius=" + placeRadius, HttpMethod.GET, entity, String.class);
        try{
            rootNode = objectMapper.readTree(response.getBody().toString());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("kakaoapi 응답변환실패!");
        }
        return PlaceAddressDto.builder()
                .name(placeName)
                .address(placeAddress)
                .x((float)(Math.floor((Float.parseFloat(x)*1000)/1000.0)))
                .y((float)(Math.floor((Float.parseFloat(y)*1000)/1000.0)))
                .detailAddress(roadAddressNode.path("region_2depth_name").asText() + " " + roadAddressNode.path("region_3depth_name").asText())
                .kakaoLink(rootNode.path("documents").get(0).path("id").asText())
                .build();
    }
}
