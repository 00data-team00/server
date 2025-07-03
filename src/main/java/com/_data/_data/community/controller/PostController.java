package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.CommentDto;
import com._data._data.community.dto.PostDetailDto;
import com._data._data.community.dto.PostDto;
import com._data._data.community.dto.PostListDto;
import com._data._data.community.dto.PostWithAuthorProfileDto;
import com._data._data.community.dto.PostWithAuthorProfileListDto;
import com._data._data.community.service.PostService;
import com._data._data.user.entity.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Post", description = "게시글 작성, 조회, 댓글, 좋아요, 타임라인 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @Operation(
        summary = "포스트 상세 조회",
        description = "포스트 ID로 포스트 상세 정보와 댓글 목록을 조회합니다."
    )
    @GetMapping("/posts/{postId}/detail")
    public ResponseEntity<PostDetailDto> getPostDetail(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Users currentUser = principal != null ? principal.getUser() : null;
        PostDetailDto postDetail = postService.getPostDetail(postId, currentUser);
        return ResponseEntity.ok(postDetail);
    }

    @Operation(
        summary = "포스트 작성",
        description = "multipart/form-data로 포스트를 작성합니다."
    )
    @PostMapping(
        value = "/me/posts",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public PostDto createPost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestPart("content") String content,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return postService.createPost(principal.getUser(), content, image);
    }

    @Operation(summary = "내가 작성한 포스트 조회", description = "인증된 사용자가 작성한 모든 포스트를 반환합니다.")
    @GetMapping("/me/posts")
    public PostListDto getMyPosts(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Users currentUser = principal.getUser();
        return toPostListDto(postService.getPostsByUser(currentUser, currentUser));
    }

    @Operation(summary = "포스트에 댓글 작성", description = "지정된 포스트에 댓글을 작성합니다.")
    @PostMapping("/me/posts/{postId}/comments")
    public CommentDto addComment(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId,
        @RequestBody String content // 단순 문자열만 받는다면 이렇게
    ) {
        return postService.addComment(principal.getUser(), postId, content);
    }

    @Operation(summary = "포스트 좋아요", description = "지정된 포스트에 좋아요를 합니다.")
    @PostMapping("/me/posts/{postId}/like")
    public void likePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.likePost(principal.getUser(), postId);
    }

    @Operation(summary = "포스트 좋아요 취소", description = "지정된 포스트의 좋아요를 취소합니다.")
    @DeleteMapping("/me/posts/{postId}/like")
    public void unlikePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.unlikePost(principal.getUser(), postId);
    }

    @Operation(summary = "전체 포스트 조회", description = "모든 사용자가 작성한 포스트를 최신순으로 반환합니다.")
    @GetMapping("/posts/timeline")
    public PostWithAuthorProfileListDto getAllPosts(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<PostWithAuthorProfileDto> posts = postService.getAllTimelineDetailed(
            principal != null ? principal.getUser() : null
        );
        PostWithAuthorProfileListDto dto = new PostWithAuthorProfileListDto();
        dto.setPosts(posts);
        return dto;
    }

    @Operation(summary = "팔로잉 타임라인 조회", description = "팔로잉 중인 유저들의 포스트를 반환합니다.")
    @GetMapping("/me/posts/timeline/following")
    public PostWithAuthorProfileListDto  getFollowingTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toPostWithAuthorProfileListDto(postService.getFollowingTimelineDetailed(principal.getUser()));
    }

    @Operation(summary = "국가별 타임라인 조회", description = "같은 국가 유저들의 포스트를 반환합니다.")
    @GetMapping("/me/posts/timeline/nation")
    public PostWithAuthorProfileListDto getNationTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toPostWithAuthorProfileListDto(postService.getNationTimelineDetailed(principal.getUser()));
    }

    @Operation(summary = "포스트 삭제", description = "지정된 포스트를 삭제합니다.")
    @DeleteMapping("/me/posts/{postId}")
    public ApiResponse deletePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.deletePost(principal.getUser(), postId);
        return new ApiResponse(true, "포스트 삭제 성공");
    }

    @Operation(
        summary = "특정 유저의 포스트 조회",
        description = "유저 ID로 해당 유저가 작성한 모든 포스트를 최신순으로 조회합니다."
    )
    @GetMapping("/users/{userId}/posts")
    public PostWithAuthorProfileListDto getUserPosts(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        List<PostWithAuthorProfileDto> posts = postService.getUserPostsDetailed(
            principal != null ? principal.getUser() : null,
            userId
        );
        return toPostWithAuthorProfileListDto(posts);
    }

    private PostListDto toPostListDto(List<PostDto> posts) {
        PostListDto postListDto = new PostListDto();
        postListDto.setPosts(posts);
        return postListDto;
    }

    private PostWithAuthorProfileListDto toPostWithAuthorProfileListDto(List<PostWithAuthorProfileDto> list) {
        PostWithAuthorProfileListDto dto = new PostWithAuthorProfileListDto();
        dto.setPosts(list);
        return dto;
    }

}
