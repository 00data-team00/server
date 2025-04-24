package com._data._data.community.repository;

import com._data._data.community.entity.Like;
import com._data._data.community.entity.Post;
import com._data._data.user.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostAndUser(Post post, Users user);
    Optional<Like> findByPostAndUser(Post post, Users user);
}
