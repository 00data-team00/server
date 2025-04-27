package com._data._data.community.dto;

import com._data._data.user.entity.Users;

/**
 * 프로필 조회 응답용 DTO
 */
public record ProfileDto(
    String name,
    String profileImage,
    Long postCount,
    Long followerCount,
    Long followingCount,
    boolean isFollowing
) {
    public static ProfileDto from(Users user, boolean isFollowing) {
        return new ProfileDto(
            user.getName(),
            user.getProfileImage(),
            (long) user.getPosts().size(),
            (long) user.getFollowers().size(),
            (long) user.getFollowing().size(),
            isFollowing
        );
    }
}