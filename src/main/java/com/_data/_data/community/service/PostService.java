package com._data._data.community.service;

import com._data._data.community.dto.CommentDto;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.entity.Like;
import com._data._data.community.entity.Comment;
import com._data._data.community.entity.Follow;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import com._data._data.community.dto.PostCreateRequest;
import com._data._data.community.dto.PostDto;
import com._data._data.community.entity.Post;
import com._data._data.community.repository.CommentRepository;
import com._data._data.community.repository.LikeRepository;
import com._data._data.community.repository.PostRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public PostDto createPost(Users user, PostCreateRequest req) {
        Post post = Post.builder()
            .author(user)
            .content(req.content())
            .imageUrl(req.imageUrl())
            .createdAt(LocalDateTime.now())
            .likeCount(0L)
            .commentCount(0L)
            .build();
        Post saved = postRepository.save(post);
        return PostDto.from(saved);
    }

    public List<PostDto> getPostsByUser(Users user) {
        return postRepository.findByAuthor(user).stream()
            .map(PostDto::from)
            .toList();
    }

    public CommentDto addComment(Users user, Long postId, String content) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트 없음"));

        Comment comment = Comment.builder()
            .post(post)
            .commenter(user)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();

        commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return CommentDto.from(comment);
    }

    public void likePost(Users user, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트 없음"));

        if (likeRepository.existsByPostAndUser(post, user)) return;

        Like like = Like.builder()
            .post(post)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();

        likeRepository.save(like);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public void unlikePost(Users user, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트 없음"));

        Like like = likeRepository.findByPostAndUser(post, user)
            .orElseThrow(() -> new EntityNotFoundException("좋아요 기록 없음"));

        likeRepository.delete(like);
        post.setLikeCount(Math.max(0L, post.getLikeCount() - 1));
        postRepository.save(post);
    }

    public List<PostDto> getFollowingTimeline(Users user) {
        List<Users> followees = user.getFollowing().stream()
            .map(Follow::getFollowee).toList();
        return postRepository.findByAuthorInOrderByCreatedAtDesc(followees)
            .stream().map(PostDto::from).toList();
    }

    public List<PostDto> getNationTimeline(Users user) {
        return postRepository.findByAuthor_NationsOrderByCreatedAtDesc(user.getNations())
            .stream().map(PostDto::from).toList();
    }

    // 🔹 유저 ID를 통해 프로필 조회 (다른 유저의 프로필을 조회할 때 사용)
    public ProfileDto getProfile(Users currentUser, Long targetUserId) {
        Users targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));
        return getProfile(currentUser, targetUser);
    }

    // 🔹 유저 객체로 바로 프로필 조회 (자기 자신 or 이미 조회한 유저가 있을 때 사용)
    public ProfileDto getProfile(Users currentUser, Users targetUser) {
        boolean isFollowing = !currentUser.equals(targetUser)
            && targetUser.getFollowers().stream()
            .anyMatch(f -> f.getFollower().equals(currentUser));

        return new ProfileDto(
            targetUser.getName(),
            targetUser.getProfileImage(),
            (long) targetUser.getPosts().size(),
            (long) targetUser.getFollowers().size(),
            (long) targetUser.getFollowing().size(),
            isFollowing
        );
    }

}
