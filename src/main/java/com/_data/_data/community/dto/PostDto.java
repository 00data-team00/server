package com._data._data.community.dto;

import com._data._data.community.entity.Post;
import java.time.LocalDateTime;

public record PostDto(
    Long id,
    Long authorId,
    String authorName,
    String content,
    String imageUrl,
    Long likeCount,
    Long commentCount,
    LocalDateTime createdAt
) {
    public static PostDto from(Post post) {
        return new PostDto(
            post.getId(),
            post.getAuthor().getId(),
            post.getAuthor().getName(),
            post.getContent(),
            post.getImageUrl(),
            post.getLikeCount(),
            post.getCommentCount(),
            post.getCreatedAt()
        );
    }
}