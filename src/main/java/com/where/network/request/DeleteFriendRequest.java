package com.where.network.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteFriendRequest {

    private Long userId;

    private Long friendId;
}
