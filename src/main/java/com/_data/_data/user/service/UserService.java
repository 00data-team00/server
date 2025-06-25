package com._data._data.user.service;

import com._data._data.auth.entity.Auth;
import com._data._data.auth.repository.AuthRepository;
import com._data._data.auth.service.MailService;
import com._data._data.community.entity.Comment;
import com._data._data.community.entity.Post;
import com._data._data.community.repository.CommentRepository;
import com._data._data.community.repository.PostRepository;
import com._data._data.user.dto.SigninRequest;
import com._data._data.user.entity.Users;
import com._data._data.user.exception.EmailAlreadyRegisteredException;
import com._data._data.user.repository.UserRepository;
import com._data._data.community.repository.LikeRepository;
import com._data._data.community.repository.FollowRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String DEFAULT_PROFILE_IMAGE = "/uploads/profile/blank.jpg";
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public boolean sendAuthcode(String email) throws MessagingException {
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        String authCode = mailService.sendSimpleMessage(email);
        if(authCode != null){
            Auth auth = authRepository.findByEmail(email);
            if(auth == null)
                authRepository.save(new Auth(email, authCode));
            else
                auth.patch(authCode);
            return true;
        }
        return false;
    }

    public boolean validationAuthcode(String email, String authCode) {
        Auth auth = authRepository.findByEmail(email);
        if (auth == null || auth.isExpired()) {
            if (auth != null) authRepository.delete(auth);
            return false;
        }
        if (auth.getAuthCode().equals(authCode)) {
            authRepository.delete(auth);
            return true;
        }
        return false;
    }

    @Transactional
    public Users register(SigninRequest req) {
        if (userRepository.existsByEmailAndIsDeletedFalse(req.email())) {
            throw new EmailAlreadyRegisteredException(req.email());
        }

        String hashed = passwordEncoder.encode(req.password());

        Users user = Users.builder()
            .email(req.email())
            .name(req.name())
            .nations(req.nations())
            .password(hashed)
            .translationLang("ko")
            .profileImage(DEFAULT_PROFILE_IMAGE)
            .build();

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Chatroom, gameInfo 삭제

        // Following, like 관계 삭제
        followRepository.deleteByFollower(user);
        followRepository.deleteByFollowee(user);
        likeRepository.deleteByUser(user);

        // Post, Comment 익명화하기
        List<Post> posts = postRepository.findByAuthor(user);
        for (Post post : posts) {
            post.setContent("[삭제된 게시글입니다]");
            post.setImageUrl(null);
            // 좋아요·댓글 수도 초기화할 수도 있고, 그냥 냅둬도 됩니다.
            postRepository.save(post);
        }

        List<Comment> comments = commentRepository.findByCommenter(user);
        for (Comment comment : comments) {
            comment.setContent("[삭제된 댓글입니다]");
            commentRepository.save(comment);
        }

        // 사용자 정보를 익명으로 변경
        user.setEmail("deleted_user_" + userId + "@deleted.com");
        user.setName("탈퇴한 사용자");
        user.setPassword("DELETED");
        user.setDeleted(true);

        userRepository.save(user);
    }
}
