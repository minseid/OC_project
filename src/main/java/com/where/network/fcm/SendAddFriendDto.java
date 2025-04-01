package com.where.network.fcm;

import com.where.entity.Meeting;
import lombok.*;
import org.checkerframework.common.value.qual.BottomVal;

import java.util.List;

@Getter
@Setter
@BottomVal
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendAddFriendDto {

    private Long Id;

    private Long friendId;

    private String friendName;

    private List<Meeting> meetings;

}
