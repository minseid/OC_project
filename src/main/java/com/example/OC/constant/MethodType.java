package com.example.OC.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MethodType {
    PlaceAdd(101),
    PlaceEdit(102),
    PlaceDelete(103),
    PlacePick(104),
    PlaceLike(105),
    ScheduleAdd(201),
    ScheduleEdit(202),
    ScheduleDelete(203),
    CommentAdd(301),
    CommentEdit(302),
    CommentDelete(303),
    MeetingAdd(401),
    MeetingEdit(402),
    MeetingDelete(403),
    MeetingAccept(404),
    FriendAdd(501),
    FriendEdit(502),
    FriendDelete(503);

    private final int code;

    @Override
    public String toString() {
        return Integer.toString(code);
    }



}


