package com._data._data.community.dto;

public record FollowDto(
    Long userId,
    String name,
    String profileImage,
    boolean isFollowing
) {}