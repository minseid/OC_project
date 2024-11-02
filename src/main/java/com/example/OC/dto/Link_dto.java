package com.example.OC.dto;


import com.example.OC.entity.Comment_Entity;
import com.example.OC.entity.Link_Entity;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Link_dto {

    private Long place_id;
    private String naver_link;
    private String kakao_link;
    private String google_link;
    public Link_Entity toEntity() {
        return new Link_Entity(place_id, naver_link, kakao_link, google_link);
    }

}
