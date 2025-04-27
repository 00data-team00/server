package com._data._data.community.dto;

import com._data._data.community.entity.Comment;
import java.time.LocalDateTime;

public record CommentDto(
    Long id,
    Long postId,
    Long commenterId,
    String commenterName,
    String content,
    LocalDateTime createdAt
) {

    public static CommentDto from(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getPost().getId(),
            comment.getCommenter().getId(),
            comment.getCommenter().getName(),
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}
