package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.service.PostService;
import com._data._data.community.service.ProfileService;
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

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProfileController {
    private final PostService postService;
    private final ProfileService profileService;

    // 타 유저 프로필 반환
    @GetMapping("/api/users/{userId}/profile")
    public ProfileDto getUserProfile(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        return postService.getProfile(principal.getUser(), userId);
    }

    // 내 프로필 반환
    @GetMapping("/api/me/profile")
    public ProfileDto getMyProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return postService.getProfile(principal.getUser(), principal.getUser().getId());
    }

    // 프로필 사진 업로드
    @PostMapping(value = "/api/me/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse uploadProfileImage(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestPart("image") MultipartFile image
    ) throws IOException {
        String url = profileService.updateProfileImage(principal.getUser(), image);
        return new ApiResponse(true, url);
    }
}
