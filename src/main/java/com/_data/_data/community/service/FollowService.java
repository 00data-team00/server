package com._data._data.community.service;

import com._data._data.community.dto.FollowDto;
import com._data._data.community.entity.Follow;
import com._data._data.community.repository.FollowRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository usersRepository;

    @Transactional
    public void follow(Users currentUser, Long targetUserId) {
        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }
        Users target = usersRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        boolean exists = followRepository.existsByFollowerAndFollowee(currentUser, target);
        if (exists) {
            throw new IllegalStateException("이미 팔로우 중인 유저입니다.");
        }

        Follow f = Follow.builder()
            .follower(currentUser)
            .followee(target)
            .createdAt(LocalDateTime.now())
            .build();
        followRepository.save(f);
    }

    @Transactional
    public void unfollow(Users currentUser, Long targetUserId) {
        Users target = usersRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        Follow f = followRepository.findByFollowerAndFollowee(currentUser, target)
            .orElseThrow(() -> new EntityNotFoundException("팔로우 기록 없음"));
        followRepository.delete(f);
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollowing(Users currentUser) {
        return followRepository.findByFollower(currentUser).stream()
            .map(f -> {
                Users u = f.getFollowee();
                return new FollowDto(
                    u.getId(),
                    u.getName(),
                    u.getProfileImage(),
                    true
                );
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollowers(Users currentUser) {
        Set<Long> imFollowingIds = followRepository.findByFollower(currentUser).stream()
            .map(f -> f.getFollowee().getId())
            .collect(Collectors.toSet());

        return followRepository.findByFollowee(currentUser).stream()
            .map(f -> {
                Users u = f.getFollower();
                boolean isFollowingBack = imFollowingIds.contains(u.getId());
                return new FollowDto(
                    u.getId(),
                    u.getName(),
                    u.getProfileImage(),
                    isFollowingBack
                );
            })
            .toList();
    }
// FollowService에 추가할 메서드들

    /**
     * 🔥 특정 유저가 팔로잉 중인 유저 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowing(Users currentUser, Long targetUserId) {
        Users targetUser = usersRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        // 현재 로그인한 유저가 팔로우하고 있는 유저들의 ID 집합
        Set<Long> currentUserFollowingIds = currentUser != null ?
            followRepository.findByFollower(currentUser).stream()
                .map(f -> f.getFollowee().getId())
                .collect(Collectors.toSet()) : Set.of();

        return followRepository.findByFollower(targetUser).stream()
            .map(f -> {
                Users u = f.getFollowee();
                // 현재 로그인한 유저가 이 유저를 팔로우하고 있는지 확인
                boolean isFollowingByCurrentUser = currentUserFollowingIds.contains(u.getId());
                return new FollowDto(
                    u.getId(),
                    u.getName(),
                    u.getProfileImage(),
                    isFollowingByCurrentUser
                );
            })
            .toList();
    }

    /**
     * 🔥 특정 유저를 팔로우하는 유저 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowers(Users currentUser, Long targetUserId) {
        Users targetUser = usersRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        // 현재 로그인한 유저가 팔로우하고 있는 유저들의 ID 집합
        Set<Long> currentUserFollowingIds = currentUser != null ?
            followRepository.findByFollower(currentUser).stream()
                .map(f -> f.getFollowee().getId())
                .collect(Collectors.toSet()) : Set.of();

        return followRepository.findByFollowee(targetUser).stream()
            .map(f -> {
                Users u = f.getFollower();
                // 현재 로그인한 유저가 이 유저를 팔로우하고 있는지 확인
                boolean isFollowingByCurrentUser = currentUserFollowingIds.contains(u.getId());
                return new FollowDto(
                    u.getId(),
                    u.getName(),
                    u.getProfileImage(),
                    isFollowingByCurrentUser
                );
            })
            .toList();
    }
}
