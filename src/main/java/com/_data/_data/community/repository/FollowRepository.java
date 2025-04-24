package com._data._data.community.repository;

import com._data._data.community.entity.Follow;
import com._data._data.user.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(Users follower, Users followee);
    List<Follow> findByFollower(Users follower);
    List<Follow> findByFollowee(Users followee);
    Optional<Follow> findByFollowerAndFollowee(Users follower, Users followee);
}
