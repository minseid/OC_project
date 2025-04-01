package com.where.service;

import com.where.constant.EntityType;
import org.springframework.stereotype.Service;

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
                    message = "1:1정보를 올바르게 입력해주세요!";
                    break;
                case Link:

                    break;
                case Meeting:
                    message = "모임정보를 올바르게 입력해주세요!";
                    break;
                case Notice:
                    message = "공지/FAQ정보를 올바르게 입력해주세요!";
                    break;
                case Participant:
                    message = "초대정보를 올바르게 입력해주세요!";
                    break;
                case Place:
                    message = "장소정보를 올바르게 입력해주세요!";
                    break;
                case Schedule:
                    message = "일정정보를 올바르게 입력해주세요!";
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
            throw new IllegalArgumentException(message);
        } else {
            return target.get();
        }
    }
}
