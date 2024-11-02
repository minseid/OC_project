package com.example.OC.dto;


import com.example.OC.entity.Comment_Entity;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


@AllArgsConstructor
@ToString
public class Comment_dto {

    private Long id;
    private long meeting_id;
    private long user_id;
    private Timestamp create_at;
    private Timestamp update_at;
    private String content;
    public Comment_Entity toEntity() {
        return new Comment_Entity(id,meeting_id,user_id,create_at,update_at,content);
    }

}
