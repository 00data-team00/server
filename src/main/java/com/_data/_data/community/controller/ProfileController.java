package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.service.PostService;
import com._data._data.community.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Profile", description = "유저 프로필 조회 및 프로필 이미지 업로드 API")
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProfileController {
    private final PostService postService;
    private final ProfileService profileService;

    @Operation(summary = "타 유저 프로필 조회",
        description = "인증된 사용자가 다른 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/api/users/{userId}/profile")
    public ProfileDto getUserProfile(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        return postService.getProfile(principal.getUser(), userId);
    }


    @Operation(summary = "내 프로필 조회",
        description = "인증된 사용자의 자신의 프로필 정보를 조회합니다.")    @GetMapping("/api/me/profile")
    public ProfileDto getMyProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return postService.getProfile(principal.getUser(), principal.getUser().getId());
    }


    @Operation(summary = "프로필 이미지 업로드",
        description = "multipart/form-data로 프로필 이미지를 업로드하고, 저장된 이미지 URL을 반환합니다.")
    @PostMapping(value = "/api/me/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse uploadProfileImage(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestPart("image") MultipartFile image
    ) throws IOException {
        String url = profileService.updateProfileImage(principal.getUser(), image);
        return new ApiResponse(true, url);
    }
}
