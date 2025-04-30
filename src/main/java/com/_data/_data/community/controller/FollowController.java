package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.FollowListDto;
import com._data._data.community.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "팔로우 관련 API")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "유저 팔로우", description = "현재 인증된 사용자가 지정한 사용자(userId)를 팔로우합니다.")
    @PostMapping("/follow/{userId}")
    public ApiResponse followUser(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        try {
            followService.follow(principal.getUser(), userId);
            return new ApiResponse(true, "팔로우 성공");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "팔로우 실패: 알 수 없는 오류");
        }
    }

    @Operation(summary = "유저 언팔로우", description = "현재 인증된 사용자가 지정한 사용자(userId)를 언팔로우합니다.")
    @DeleteMapping("/follow/{userId}")
    public ApiResponse unfollowUser(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        try {
            followService.unfollow(principal.getUser(), userId);
            return new ApiResponse(true, "언팔로우 성공");
        } catch (IllegalArgumentException e) {
            return new ApiResponse(false, e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "언팔로우 실패: 알 수 없는 오류");
        }
    }

    @Operation(summary = "내가 팔로잉 중인 유저 목록 조회", description = "현재 인증된 사용자가 팔로잉 중인 유저 목록을 가져옵니다.")
    @GetMapping("/following")
    public FollowListDto getFollowing(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return new FollowListDto(followService.getFollowing(principal.getUser()));
    }

    @Operation(summary = "나를 팔로우하는 유저 목록 조회", description = "현재 인증된 사용자를 팔로우하는 유저 목록을 가져옵니다.")
    @GetMapping("/followers")
    public FollowListDto getFollowers(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return new FollowListDto(followService.getFollowers(principal.getUser()));
    }
}
