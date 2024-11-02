package com.example.OC.dto;


import com.example.OC.entity.Mapping_Entity;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Mapping_dto {

    private Long id;
    private long meeting_id;
    private long user_id;
    public Mapping_Entity toEntity() {
        return new Mapping_Entity(id,meeting_id,user_id);
    }
}
