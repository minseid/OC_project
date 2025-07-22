package com.where.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class kakaoshareRequest {

    private String CHAT_TYPE;
    private String HASH_CHAT_ID;
    private Long TEMPLATE_ID;
    private Long meetingId;

}
