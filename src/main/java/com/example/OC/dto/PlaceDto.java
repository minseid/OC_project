package com.example.OC.dto;


import com.example.OC.entity.PlaceEntity;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@ToString
public class PlaceDto {

    private Long id;
    private long meeting_id;
    private long create_user;
    private String place_name;
    private String address;
    private Timestamp create_at;
    private Timestamp update_at;
    private long like_count;
    public PlaceEntity toEntity() {
        return new PlaceEntity(id,meeting_id,create_user,place_name,address,create_at,update_at,like_count);
    }

}
