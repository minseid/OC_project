package com.where.network.response;

import com.where.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private boolean isMine;

    public static GetCommentResponse toDto(Comment comment) {
        return GetCommentResponse.builder()
                .id(comment.getId())
                .placeId(comment.getPlace().getId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
