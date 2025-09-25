package com._data._data.community.repository;

import com._data._data.community.entity.Post;
import com._data._data.user.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(Users author);
    // 모든 포스트를 Author + Nation과 함께 조회
    @Query("SELECT p FROM Post p " +
        "JOIN FETCH p.author " +
        "ORDER BY p.createdAt DESC")
    List<Post> findAllWithAuthorAndNation();

    // 특정 작성자들의 포스트를 Author + Nation과 함께 조회
    @Query("SELECT p FROM Post p " +
        "JOIN FETCH p.author a " +
        "WHERE a IN :authors " +
        "ORDER BY p.createdAt DESC")
    List<Post> findByAuthorInWithAuthorAndNation(@Param("authors") List<Users> authors);

    // 특정 국가의 포스트를 Author + Nation과 함께 조회
    @Query("SELECT p FROM Post p " +
        "JOIN FETCH p.author " +
        "WHERE p.author.nations = :nationId " +
        "ORDER BY p.createdAt DESC")
    List<Post> findByAuthorNationsWithAuthorAndNation(@Param("nationId") Long nationId);

    // 특정 작성자의 포스트를 Author + Nation과 함께 조회
    @Query("SELECT p FROM Post p " +
        "JOIN FETCH p.author " +
        "WHERE p.author = :author " +
        "ORDER BY p.createdAt DESC")
    List<Post> findByAuthorWithAuthorAndNation(@Param("author") Users author);

    @Query("SELECT p FROM Post p " +
        "JOIN FETCH p.author " +
        "WHERE p.id = :postId")
    Optional<Post> findByIdWithAuthor(@Param("postId") Long postId);
}
