package com.example.OC.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkFriendRequest {

    private Long userId;

    private Long friendId;
}
