package com.example.OC.network.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkFriendResponse {

    private Long userId;

    private Long friendId;

    private boolean bookmark;
}
