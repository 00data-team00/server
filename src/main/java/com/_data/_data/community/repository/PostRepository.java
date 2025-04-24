package com._data._data.community.repository;

import com._data._data.community.entity.Post;
import com._data._data.user.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(Users author);
    List<Post> findByAuthorInOrderByCreatedAtDesc(List<Users> authors);
    List<Post> findByAuthor_NationsOrderByCreatedAtDesc(Long nations);
}
