package com._data._data.community.repository;

import com._data._data.community.entity.Follow;
import com._data._data.user.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(Users follower, Users followee);
    List<Follow> findByFollower(Users follower);
    List<Follow> findByFollowee(Users followee);
    Optional<Follow> findByFollowerAndFollowee(Users follower, Users followee);

    // 내가 팔로우(follower) 한 모든 관계 삭제
    @Modifying
    @Transactional
    void deleteByFollower(Users follower);

    // 나를 팔로우(followee) 한 모든 관계 삭제
    @Modifying
    @Transactional
    void deleteByFollowee(Users followee);

    // 특정 팔로워가 여러 유저들을 팔로우하는지 한 번에 조회
    @Query("SELECT f FROM Follow f WHERE f.follower = :follower AND f.followee.id IN :followeeIds")
    List<Follow> findByFollowerAndFolloweeIdIn(@Param("follower") Users follower, @Param("followeeIds") List<Long> followeeIds);


}
