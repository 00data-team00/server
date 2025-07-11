package com._data._data.community.service;

import com._data._data.community.dto.CommentDto;
import com._data._data.community.dto.PostAuthorProfileDto;
import com._data._data.community.dto.PostDetailDto;
import com._data._data.community.dto.PostWithAuthorProfileDto;
import com._data._data.community.dto.ProfileDto;
import com._data._data.community.entity.Like;
import com._data._data.community.entity.Comment;
import com._data._data.community.entity.Follow;
import com._data._data.community.repository.FollowRepository;
import com._data._data.user.entity.Nation;
import com._data._data.user.service.NationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NationService nationService;
    private final FirebaseFileService firebaseFileService;

    @Transactional
    public PostDto createPost(Users user, String content, MultipartFile image) throws IOException {
        Post post = Post.builder()
            .author(user)
            .content(content)
            .createdAt(LocalDateTime.now())
            .likeCount(0L)
            .commentCount(0L)
            .build();
        Post saved = postRepository.save(post);

        if (image != null && !image.isEmpty()) {
            String imageUrl = firebaseFileService.store(image, "post", user.getId(), saved.getId());
            saved.setImageUrl(imageUrl);
            postRepository.save(saved);
        }
        return PostDto.from(saved);
    }

    @Transactional
    public void deletePost(Users user, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트를 찾을 수 없습니다."));

        if (!post.getAuthor().equals(user)) {
            throw new IllegalStateException("본인이 작성한 포스트만 삭제할 수 있습니다.");

        }

        commentRepository.deleteByPost(post);
        likeRepository.deleteByPost(post);

        // Firebase Storage에서 이미지 삭제
        if (post.getImageUrl() != null) {
            // URL에서 파일 경로 추출 (Firebase Storage 경로 추출 로직 필요)
            String fileName = extractFileNameFromUrl(post.getImageUrl());
            firebaseFileService.delete(fileName);
        }
        postRepository.delete(post);
    }

    public List<PostDto> getAllPosts(Users currentUser) {
        return postRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(post -> {
                boolean isLiked = currentUser != null &&
                    likeRepository.existsByPostAndUser(post, currentUser);
                return PostDto.fromWithLiked(post, isLiked);
            })
            .toList();
    }

    /**
     * Firebase Storage URL에서 파일 경로 추출
     * 예: https://storage.googleapis.com/bucket/post/filename.jpg -> post/filename.jpg
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Firebase Storage URL 패턴에서 파일 경로 추출
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                // 마지막 두 부분을 합쳐서 "post/filename.jpg" 형태로 만들기
                return parts[parts.length - 2] + "/" + parts[parts.length - 1].split("\\?")[0];
            }
        } catch (Exception e) {
            System.err.println("URL에서 파일명 추출 실패: " + e.getMessage());
        }

        return null;
    }

    public List<PostDto> getPostsByUser(Users user, Users currentUser) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(user).stream()
            .map(post -> {
                boolean isLiked = currentUser != null &&
                    likeRepository.existsByPostAndUser(post, currentUser);
                return PostDto.fromWithLiked(post, isLiked);
            })
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

    @Transactional(readOnly = true)
    public List<PostDto> getFollowingTimeline(Users user) {
        List<Users> followees = followRepository.findByFollower(user).stream()
            .map(Follow::getFollowee)
            .toList();
        if (followees.isEmpty()) {
            return List.of();
        }
        return postRepository.findByAuthorInOrderByCreatedAtDesc(followees)
            .stream()
            .map(post -> {
                boolean isLiked = likeRepository.existsByPostAndUser(post, user);
                return PostDto.fromWithLiked(post, isLiked);
            })
            .toList();
    }

    public List<PostDto> getNationTimeline(Users user) {
        return postRepository.findByAuthor_NationsOrderByCreatedAtDesc(user.getNations())
            .stream()
            .map(post -> {
                boolean isLiked = likeRepository.existsByPostAndUser(post, user);
                return PostDto.fromWithLiked(post, isLiked);
            })
            .toList();
    }


    // 🔹 유저 ID를 통해 프로필 조회 (다른 유저의 프로필을 조회할 때 사용)
    public ProfileDto getProfile(Users currentUser, Long targetUserId) {
        Users targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));
        return getProfile(currentUser, targetUser);
    }

    public ProfileDto getProfile(Users currentUser, Users targetUser) {
        // 🔥 null 체크 추가
        boolean isFollowing = currentUser != null
            && !currentUser.equals(targetUser)
            && followRepository.existsByFollowerAndFollowee(currentUser, targetUser);

        Nation nation = nationService.getNationById(targetUser.getNations());
        String nationName = nation != null ? nation.getName() : "Unknown";
        String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

        return ProfileDto.from(targetUser, isFollowing, nationName, nationNameKo);
    }

    /**
     * 팔로잉 타임라인: Post + author profile + isLiked
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getFollowingTimelineDetailed(Users currentUser) {
        List<Users> followees = followRepository.findByFollower(currentUser)
            .stream().map(f -> f.getFollowee()).toList();

        if (followees.isEmpty()) {
            return List.of();
        }
        // Fetch posts by followees
        List<Post> posts = postRepository.findByAuthorInOrderByCreatedAtDesc(followees);


        return posts.stream().map(post -> {
            boolean isFollowing = true; // by definition, author is followed
            boolean isLiked = likeRepository.existsByPostAndUser(post, currentUser);

            // 🔥 수정: PostDto에도 isLiked 적용
            PostDto dto = PostDto.fromWithLiked(post, isLiked);

            // 🔥 국가 정보 추가
            Nation nation = nationService.getNationById(post.getAuthor().getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            var authorProfile = new PostAuthorProfileDto(
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getAuthor().getProfileImage(),
                (long) post.getAuthor().getPosts().size(),
                (long) post.getAuthor().getFollowers().size(),
                (long) post.getAuthor().getFollowing().size(),
                isFollowing,
                nationName,    // 🔥 추가
                nationNameKo   // 🔥 추가
            );
            return new PostWithAuthorProfileDto(dto, authorProfile);
        }).toList();
    }

    /**
     * 국가별 타임라인: Post + author profile + isLiked
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getNationTimelineDetailed(Users currentUser) {
        List<Post> posts = postRepository.findByAuthor_NationsOrderByCreatedAtDesc(currentUser.getNations());

        return posts.stream().map(post -> {
            boolean isFollowing = followRepository.existsByFollowerAndFollowee(currentUser, post.getAuthor());
            boolean isLiked = likeRepository.existsByPostAndUser(post, currentUser);

            PostDto dto = PostDto.fromWithLiked(post, isLiked);

            // 🔥 국가 정보 추가
            Nation nation = nationService.getNationById(post.getAuthor().getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            var authorProfile = new PostAuthorProfileDto(
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getAuthor().getProfileImage(),
                (long) post.getAuthor().getPosts().size(),
                (long) post.getAuthor().getFollowers().size(),
                (long) post.getAuthor().getFollowing().size(),
                isFollowing,
                nationName,    // 🔥 추가
                nationNameKo   // 🔥 추가
            );
            return new PostWithAuthorProfileDto(dto, authorProfile);
        }).toList();
    }

    /**
     * 전체 타임라인: Post + author profile + isLiked
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getAllTimelineDetailed(Users currentUser) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream().map(post -> {
            boolean isFollowing = currentUser != null &&
                followRepository.existsByFollowerAndFollowee(currentUser, post.getAuthor());
            boolean isLiked = currentUser != null &&
                likeRepository.existsByPostAndUser(post, currentUser);

            // 🔥 수정: PostDto에도 isLiked 적용
            PostDto dto = PostDto.fromWithLiked(post, isLiked);

            Nation nation = nationService.getNationById(post.getAuthor().getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            var authorProfile = new PostAuthorProfileDto(
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getAuthor().getProfileImage(),
                (long) post.getAuthor().getPosts().size(),
                (long) post.getAuthor().getFollowers().size(),
                (long) post.getAuthor().getFollowing().size(),
                isFollowing,
                nationName,
                nationNameKo
            );
            return new PostWithAuthorProfileDto(dto, authorProfile);
        }).toList();
    }


    /**
     * 🔥 새로 추가: 포스트 상세 조회 (댓글 포함)
     */
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId, Users currentUser) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트를 찾을 수 없습니다."));

        // 댓글을 오래된 순으로 조회 (일반적인 댓글 순서)
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        List<CommentDto> commentDtos = comments.stream()
            .map(CommentDto::from)
            .toList();

        // 현재 유저가 이 포스트에 좋아요를 눌렀는지 확인
        boolean isLiked = currentUser != null &&
            likeRepository.existsByPostAndUser(post, currentUser);

        return PostDetailDto.fromWithComments(post, commentDtos, isLiked);    }
    /**
     * 🔥 특정 유저의 포스트를 최신순으로 조회 (상세 정보 포함)
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getUserPostsDetailed(Users currentUser, Long targetUserId) {
        Users targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        // 해당 유저의 포스트를 최신순으로 조회
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(targetUser);

        return posts.stream().map(post -> {
            boolean isFollowing = currentUser != null &&
                followRepository.existsByFollowerAndFollowee(currentUser, targetUser);
            boolean isLiked = currentUser != null &&
                likeRepository.existsByPostAndUser(post, currentUser);
            PostDto dto = PostDto.fromWithLiked(post, isLiked);

            // 🔥 국가 정보 추가
            Nation nation = nationService.getNationById(targetUser.getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            var authorProfile = new PostAuthorProfileDto(
                targetUser.getId(),
                targetUser.getName(),
                targetUser.getProfileImage(),
                (long) targetUser.getPosts().size(),
                (long) targetUser.getFollowers().size(),
                (long) targetUser.getFollowing().size(),
                isFollowing,
                nationName,
                nationNameKo
            );
            return new PostWithAuthorProfileDto(dto, authorProfile);
        }).toList();
    }
}
