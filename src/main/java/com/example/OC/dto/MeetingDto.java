package com.example.OC.dto;


import com.example.OC.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@ToString
public class MeetingDto {

    private Long id;
    private String title;
    private String link;
    private Timestamp created_at;
    public Meeting toEntity() {
        return new Meeting(id, title, link, created_at);
    }
}
