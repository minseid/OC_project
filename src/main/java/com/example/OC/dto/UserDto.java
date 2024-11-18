package com.example.OC.dto;


import com.example.OC.entity.User;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@AllArgsConstructor
@ToString
public class UserDto {

    private Long id;
    private String User_name;
    private String User_nickname;
    private String User_email;
    private String User_img;
    private Timestamp User_create_at;
    private long USer_token;
    private boolean User_alarm;
    public User toEntity(){
        return new User(id,User_name,User_nickname,User_email,User_img,User_create_at,USer_token,User_alarm);
    }
}
