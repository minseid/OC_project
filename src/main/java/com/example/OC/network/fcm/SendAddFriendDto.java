package com.example.OC.network.fcm;

import com.example.OC.entity.Meeting;
import jakarta.validation.constraints.NotNull;
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
