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
    boolean isFollowing,
    String nationName,     // 🔥 추가: 국가 영문명
    String nationNameKo
) {
    public static ProfileDto from(Users user, boolean isFollowing, String nationName, String nationNameKo) {
        return new ProfileDto(
            user.getName(),
            user.getProfileImage(),
            (long) user.getPosts().size(),
            (long) user.getFollowers().size(),
            (long) user.getFollowing().size(),
            isFollowing,
            nationName,
            nationNameKo
        );
    }
}