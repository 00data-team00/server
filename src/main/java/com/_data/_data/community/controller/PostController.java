package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.community.dto.CommentDto;
import com._data._data.community.dto.PostCreateRequest;
import com._data._data.community.dto.PostDto;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // === 포스트 작성 ===
    @PostMapping("")
    public PostDto createPost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestBody PostCreateRequest request
    ) {
        return postService.createPost(principal.getUser(), request);
    }

    // === 내가 작성한 포스트 조회 ===
    @GetMapping("")
    public List<PostDto> getMyPosts(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return postService.getPostsByUser(principal.getUser());
    }

    // === 포스트에 댓글 작성 ===
    @PostMapping("/{postId}/comments")
    public CommentDto addComment(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId,
        @RequestBody String content // 단순 문자열만 받는다면 이렇게
    ) {
        return postService.addComment(principal.getUser(), postId, content);
    }

    // === 포스트 좋아요 누르기 ===
    @PostMapping("/{postId}/like")
    public void likePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.likePost(principal.getUser(), postId);
    }

    // === 포스트 좋아요 취소 ===
    @DeleteMapping("/{postId}/like")
    public void unlikePost(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long postId
    ) {
        postService.unlikePost(principal.getUser(), postId);
    }

    // === 타임라인: 팔로잉 유저들의 포스트 ===
    @GetMapping("/timeline/following")
    public List<PostDto> getFollowingTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return postService.getFollowingTimeline(principal.getUser());
    }

    // === 타임라인: 같은 국가 유저들의 포스트 ===
    @GetMapping("/timeline/nation")
    public List<PostDto> getNationTimeline(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return postService.getNationTimeline(principal.getUser());
    }



}
