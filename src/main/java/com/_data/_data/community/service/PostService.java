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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByUser(Users user, Users currentUser) {
        List<Post> posts = postRepository.findByAuthorWithAuthorAndNation(user);
        if (posts.isEmpty()) {
            return List.of();
        }
        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(posts, currentUser);
        return posts.stream()
            .map(post -> PostDto.fromWithLiked(post, likeStatusMap.get(post.getId())))
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

    // like 배치 조회
    private Map<Long, Boolean> getLikeStatusMap(List<Post> posts, Users currentUser) {
        if (currentUser == null) {
            return posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> false));
        }

        List<Long> postIds = posts.stream()
            .map(Post::getId)
            .toList();

        List<Like> likes = likeRepository.findByPostIdInAndUser(postIds, currentUser);
        Set<Long> likedPostIds = likes.stream()
            .map(like -> like.getPost().getId())
            .collect(Collectors.toSet());

        return posts.stream()
            .collect(Collectors.toMap(
                Post::getId,
                post -> likedPostIds.contains(post.getId())
            ));
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

    // 유저 ID를 통해 프로필 조회 (다른 유저의 프로필을 조회할 때 사용)
    public ProfileDto getProfile(Users currentUser, Long targetUserId) {
        Users targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));
        return getProfile(currentUser, targetUser);
    }

    public ProfileDto getProfile(Users currentUser, Users targetUser) {
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
            .stream().map(Follow::getFollowee).toList();

        if (followees.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRepository.findByAuthorInWithAuthorAndNation(followees);

        if (posts.isEmpty()) {
            return List.of();
        }

        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(posts, currentUser);

        return posts.stream().map(post -> {
            boolean isLiked = likeStatusMap.get(post.getId());
            PostDto postDto = PostDto.fromWithLiked(post, isLiked);

            Users author = post.getAuthor();
            Nation nation = nationService.getNationById(author.getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            PostAuthorProfileDto authorProfile = new PostAuthorProfileDto(
                author.getId(),
                author.getName(),
                author.getProfileImage(),
                (long) author.getPosts().size(),
                (long) author.getFollowers().size(),
                (long) author.getFollowing().size(),
                true, // 팔로잉 타임라인이므로 항상 true
                nationName,
                nationNameKo
            );
            return new PostWithAuthorProfileDto(postDto, authorProfile);
        }).toList();
    }


    /**
     * 국가별 타임라인: Post + author profile + isLiked
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getNationTimelineDetailed(Users currentUser) {
        List<Post> posts = postRepository.findByAuthorNationsWithAuthorAndNation(currentUser.getNations());

        if (posts.isEmpty()) {
            return List.of();
        }

        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(posts, currentUser);
        List<Users> authors = posts.stream()
            .map(Post::getAuthor)
            .distinct()
            .toList();
        Map<Long, Boolean> followStatusMap = getFollowStatusMap(authors, currentUser);

        return posts.stream().map(post -> {
            boolean isLiked = likeStatusMap.get(post.getId());
            boolean isFollowing = followStatusMap.get(post.getAuthor().getId());
            PostDto postDto = PostDto.fromWithLiked(post, isLiked);

            Users author = post.getAuthor();
            Nation nation = nationService.getNationById(author.getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            PostAuthorProfileDto authorProfile = new PostAuthorProfileDto(
                author.getId(),
                author.getName(),
                author.getProfileImage(),
                (long) author.getPosts().size(),
                (long) author.getFollowers().size(),
                (long) author.getFollowing().size(),
                isFollowing,
                nationName,
                nationNameKo
            );
            return new PostWithAuthorProfileDto(postDto, authorProfile);
        }).toList();
    }

    // follow 배치조회
    private Map<Long, Boolean> getFollowStatusMap(List<Users> authors, Users currentUser) {
        if (currentUser == null) {
            return authors.stream()
                .collect(Collectors.toMap(Users::getId, user -> false));
        }

        List<Long> authorIds = authors.stream()
            .map(Users::getId)
            .toList();

        List<Follow> follows = followRepository.findByFollowerAndFolloweeIdIn(currentUser, authorIds);
        Set<Long> followedAuthorIds = follows.stream()
            .map(follow -> follow.getFollowee().getId())
            .collect(Collectors.toSet());

        return authors.stream()
            .collect(Collectors.toMap(
                Users::getId,
                user -> followedAuthorIds.contains(user.getId())
            ));
    }

    /**
     * 전체 타임라인: Post + author profile + isLiked
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getAllTimelineDetailed(Users currentUser) {
        List<Post> posts = postRepository.findAllWithAuthorAndNation();

        if (posts.isEmpty()) {
            return List.of();
        }

        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(posts, currentUser);
        List<Users> authors = posts.stream()
            .map(Post::getAuthor)
            .distinct()
            .toList();
        Map<Long, Boolean> followStatusMap = getFollowStatusMap(authors, currentUser);

        return posts.stream().map(post -> {
            boolean isLiked = likeStatusMap.get(post.getId());
            boolean isFollowing = followStatusMap.get(post.getAuthor().getId());
            PostDto postDto = PostDto.fromWithLiked(post, isLiked);

            Users author = post.getAuthor();
            Nation nation = nationService.getNationById(author.getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            PostAuthorProfileDto authorProfile = new PostAuthorProfileDto(
                author.getId(),
                author.getName(),
                author.getProfileImage(),
                (long) author.getPosts().size(),
                (long) author.getFollowers().size(),
                (long) author.getFollowing().size(),
                isFollowing,
                nationName,
                nationNameKo
            );
            return new PostWithAuthorProfileDto(postDto, authorProfile);
        }).toList();
    }

    /**
     * 포스트 상세 조회 (댓글 포함)
     */
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId, Users currentUser) {
        // 1. Post + Author를 한 번에 조회
        Post post = postRepository.findByIdWithAuthor(postId)
            .orElseThrow(() -> new EntityNotFoundException("포스트를 찾을 수 없습니다."));

        // 2. 댓글들을 한 번에 조회 (댓글 작성자 정보 포함)
        List<Comment> comments = commentRepository.findByPostWithCommenterOrderByCreatedAtAsc(post);
        List<CommentDto> commentDtos = comments.stream()
            .map(CommentDto::from)
            .toList();

        // 3. Like 상태 확인 (단건이므로 기존 방식 유지)
        boolean isLiked = currentUser != null &&
            likeRepository.existsByPostAndUser(post, currentUser);

        return PostDetailDto.fromWithComments(post, commentDtos, isLiked);
    }

    /**
     * 특정 유저의 포스트를 최신순으로 조회 (상세 정보 포함)
     */
    @Transactional(readOnly = true)
    public List<PostWithAuthorProfileDto> getUserPostsDetailed(Users currentUser, Long targetUserId) {
        // 1. 대상 유저 조회
        Users targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        // 2. 해당 유저의 Post + Author + Nation을 한 번에 조회
        List<Post> posts = postRepository.findByAuthorWithAuthorAndNation(targetUser);

        if (posts.isEmpty()) {
            return List.of();
        }

        // 3. Like 정보를 배치로 조회
        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(posts, currentUser);

        // 4. Follow 상태 확인 (단일 유저이므로 한 번만 조회)
        boolean isFollowing = currentUser != null &&
            followRepository.existsByFollowerAndFollowee(currentUser, targetUser);

        // 5. DTO 변환
        return posts.stream().map(post -> {
            boolean isLiked = likeStatusMap.get(post.getId());
            PostDto postDto = PostDto.fromWithLiked(post, isLiked);

            Nation nation = nationService.getNationById(targetUser.getNations());
            String nationName = nation != null ? nation.getName() : "Unknown";
            String nationNameKo = nation != null ? nation.getNameKo() : "알 수 없음";

            PostAuthorProfileDto authorProfile = new PostAuthorProfileDto(
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
            return new PostWithAuthorProfileDto(postDto, authorProfile);
        }).toList();
    }
}
