package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.CommentDto;
import com._data._data.community.dto.PostDto;
import com._data._data.community.dto.PostListDto;
import com._data._data.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Post", description = "게시글 작성, 조회, 댓글, 좋아요, 타임라인 API")
@RestController
@RequestMapping("/api/me/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(
        summary = "포스트 작성",
        description = "multipart/form-data로 포스트를 작성합니다."
    )    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostDto createPost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestPart("content") String content,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return postService.createPost(principal.getUser(), content, image);
    }

    @Operation(summary = "내가 작성한 포스트 조회", description = "인증된 사용자가 작성한 모든 포스트를 반환합니다.")
    @GetMapping("")
    public PostListDto getMyPosts(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toPostListDto(postService.getPostsByUser(principal.getUser()));
    }

    @Operation(summary = "포스트에 댓글 작성", description = "지정된 포스트에 댓글을 작성합니다.")
    @PostMapping("/{postId}/comments")
    public CommentDto addComment(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId,
        @RequestBody String content // 단순 문자열만 받는다면 이렇게
    ) {
        return postService.addComment(principal.getUser(), postId, content);
    }

    @Operation(summary = "포스트 좋아요", description = "지정된 포스트에 좋아요를 합니다.")
    @PostMapping("/{postId}/like")
    public void likePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.likePost(principal.getUser(), postId);
    }

    @Operation(summary = "포스트 좋아요 취소", description = "지정된 포스트의 좋아요를 취소합니다.")
    @DeleteMapping("/{postId}/like")
    public void unlikePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.unlikePost(principal.getUser(), postId);
    }

    @Operation(summary = "팔로잉 타임라인 조회", description = "팔로잉 중인 유저들의 포스트를 반환합니다.")
    @GetMapping("/timeline/following")
    public PostListDto getFollowingTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toPostListDto(postService.getFollowingTimeline(principal.getUser()));
    }

    @Operation(summary = "국가별 타임라인 조회", description = "같은 국가 유저들의 포스트를 반환합니다.")
    @GetMapping("/timeline/nation")
    public PostListDto getNationTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toPostListDto(postService.getNationTimeline(principal.getUser()));
    }

    @Operation(summary = "포스트 삭제", description = "지정된 포스트를 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ApiResponse deletePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.deletePost(principal.getUser(), postId);
        return new ApiResponse(true, "포스트 삭제 성공");
    }

    private PostListDto toPostListDto(List<PostDto> posts) {
        PostListDto postListDto = new PostListDto();
        postListDto.setPosts(posts);
        return postListDto;
    }
}
