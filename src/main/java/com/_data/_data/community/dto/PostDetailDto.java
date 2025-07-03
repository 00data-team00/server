package com._data._data.community.dto;

import com._data._data.community.entity.Post;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 포스트 상세 조회용 DTO (댓글 포함)
 */
public record PostDetailDto(
    Long id,
    Long authorId,
    String authorName,
    String authorProfileImage,
    String content,
    String imageUrl,
    Long likeCount,
    Long commentCount,
    LocalDateTime createdAt,
    List<CommentDto> comments, // 🔥 댓글 목록
    boolean isLiked

) {
    public static PostDetailDto fromWithComments(Post post, List<CommentDto> comments, boolean isLiked) {
        return new PostDetailDto(
            post.getId(),
            post.getAuthor().getId(),
            post.getAuthor().getName(),
            post.getAuthor().getProfileImage(), // 🔥 작성자 프로필 이미지 추가
            post.getContent(),
            post.getImageUrl(),
            post.getLikeCount(),
            post.getCommentCount(),
            post.getCreatedAt(),
            comments,
            isLiked
        );
    }
    public static PostDetailDto fromWithComments(Post post, List<CommentDto> comments) {
        return fromWithComments(post, comments, false);
    }
}