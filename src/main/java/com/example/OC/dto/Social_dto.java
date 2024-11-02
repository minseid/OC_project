package com.example.OC.dto;


import com.example.OC.entity.Social_Entity;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@ToString
public class Social_dto {

    private Long id;
    private String title;
    private String link;
    private Timestamp created_at;
    public Social_Entity toEntity() {
        return new Social_Entity(id, title, link, created_at);
    }
}
