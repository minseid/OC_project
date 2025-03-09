package com.example.OC.service;

import com.example.OC.constant.EntityType;
import com.example.OC.entity.*;
import com.example.OC.repository.*;
import jakarta.persistence.MappedSuperclass;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.Optional;

@Service
public class FindService {

    public <T> T valid(Optional<T> target, EntityType entityType) {
        String message = null;
        if(target.isEmpty()) {
            switch (entityType) {
                case Comment:
                    message = "코멘트정보를 올바르게 입력해주세요!";
                    break;
                case Friend:
                    message = "친구정보를 올바르게 입력해주세요!";
                    break;
                case Inquiry:

                    break;
                case Link:

                    break;
                case Meeting:
                    message = "모임정보를 올바르게 입력해주세요!";
                    break;
                case Notice:

                    break;
                case Participant:

                    break;
                case Place:
                    message = "장소정보를 올바르게 입력해주세요!";
                    break;
                case Schedule:

                    break;
                case User:
                    message = "유저정보를 올바르게 입력해주세요!";
                    break;
                case UserMeetingMapping:
                    message = "해당유저는 해당모임에 가입되어있지 않습니다!";
                    break;
                default:
                    break;
            }
            throw new IllegalArgumentException();
        } else {
            return target.get();
        }
    }
}
