package com._data._data.community.repository;

import com._data._data.community.entity.Post;
import com._data._data.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com._data._data.user.entity.Users;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByPost(Post post);
    List<Comment> findByCommenter(Users commenter);
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

}
