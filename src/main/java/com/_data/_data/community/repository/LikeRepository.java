package com._data._data.community.repository;

import com._data._data.community.entity.Like;
import com._data._data.community.entity.Post;
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
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostAndUser(Post post, Users user);
    Optional<Like> findByPostAndUser(Post post, Users user);
    void deleteByPost(Post post);

    // 사용자가 누른 모든 좋아요 삭제
    @Modifying
    @Transactional
    void deleteByUser(Users user);

    // 여러 포스트에 대한 한 유저의 좋아요 조회
    @Query("SELECT l FROM Like l WHERE l.post.id IN :postIds AND l.user = :user")
    List<Like> findByPostIdInAndUser(@Param("postIds") List<Long> postIds, @Param("user") Users user);

}
