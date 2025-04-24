package com._data._data.community.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {
    private final PostService postService;

    @GetMapping("/{userId}/profile")
    public ProfileDto getUserProfile(
        @AuthenticationPrincipal CustomUserDetails principal,
        @PathVariable Long userId
    ) {
        return postService.getProfile(principal.getUser(), userId);
    }

    @GetMapping("/me/profile")
    public ProfileDto getMyProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return postService.getProfile(principal.getUser(), principal.getUser().getId());
    }
}
