package com.example.OC.dto;


import com.example.OC.entity.LinkEntity;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class LinkDto {

    private Long place_id;
    private String naver_link;
    private String kakao_link;
    private String google_link;
    public LinkEntity toEntity() {
        return new LinkEntity(place_id, naver_link, kakao_link, google_link);
    }

}
