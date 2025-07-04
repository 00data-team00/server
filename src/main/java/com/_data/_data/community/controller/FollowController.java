package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.FollowDto;
import com._data._data.community.dto.FollowListDto;
import com._data._data.community.service.FollowService;
import com._data._data.user.entity.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "팔로우 관련 API")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "유저 팔로우", description = "현재 인증된 사용자가 지정한 사용자(userId)를 팔로우합니다.")
    @PostMapping("/me/follow/{userId}")
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
    @DeleteMapping("/me/follow/{userId}")
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
    @GetMapping("/me/following")
    public FollowListDto getFollowing(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toFollowListDto(followService.getFollowing(principal.getUser()));
    }

    @Operation(summary = "나를 팔로우하는 유저 목록 조회", description = "현재 인증된 사용자를 팔로우하는 유저 목록을 가져옵니다.")
    @GetMapping("/me/followers")
    public FollowListDto getFollowers(
        @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return toFollowListDto(followService.getFollowers(principal.getUser()));
    }

    // FollowController에 추가할 엔드포인트들

    @Operation(
        summary = "특정 유저가 팔로잉 중인 유저 목록 조회",
        description = "지정된 유저 ID가 팔로잉 중인 유저 목록을 가져옵니다."
    )
    @GetMapping("/users/{userId}/following")
    public FollowListDto getUserFollowing(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        Users currentUser = principal != null ? principal.getUser() : null;
        return toFollowListDto(followService.getFollowing(currentUser, userId));
    }

    @Operation(
        summary = "특정 유저를 팔로우하는 유저 목록 조회",
        description = "지정된 유저 ID를 팔로우하는 유저 목록을 가져옵니다."
    )
    @GetMapping("/users/{userId}/followers")
    public FollowListDto getUserFollowers(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        Users currentUser = principal != null ? principal.getUser() : null;
        return toFollowListDto(followService.getFollowers(currentUser, userId));
    }

    private FollowListDto toFollowListDto(List<FollowDto> followList) {
        FollowListDto followListDto = new FollowListDto();
        followListDto.setFollowList(followList);
        return followListDto;
    }
}
