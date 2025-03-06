package com.example.OC.network.response;

import com.example.OC.entity.Comment;
import com.example.OC.entity.Place;
import com.example.OC.entity.TimeBaseEntity;
import com.example.OC.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCommentResponse {

    @NotNull
    private Long id;

    @NotNull
    private Long placeId;

    @NotBlank
    private String description;

    public static GetCommentResponse toDto(Comment comment) {
        return GetCommentResponse.builder()
                .id(comment.getId())
                .placeId(comment.getPlace().getId())
                .description(comment.getDescription())
                .build();
    }
}
