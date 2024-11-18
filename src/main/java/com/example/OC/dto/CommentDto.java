package com.example.OC.dto;


import com.example.OC.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


@AllArgsConstructor
@ToString
public class CommentDto {

    private Long id;
    private long meetingId;
    private long userId;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String content;
    public Comment toEntity() {
        return new Comment(id,meetingId,userId,createAt,updateAt,content);
    }

}
