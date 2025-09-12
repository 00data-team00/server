package com._data._data.community.repository;

import com._data._data.community.entity.Post;
import com._data._data.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import com._data._data.user.entity.Users;
import org.springframework.data.repository.query.Param;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByPost(Post post);
    List<Comment> findByCommenter(Users commenter);
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    // 포스트의 댓글들을 댓글 작성자 정보와 함께 조회
    @Query("SELECT c FROM Comment c " +
        "JOIN FETCH c.commenter " +
        "WHERE c.post = :post " +
        "ORDER BY c.createdAt ASC")
    List<Comment> findByPostWithCommenterOrderByCreatedAtAsc(@Param("post") Post post);
}
