package com._data._data.user.entity;

import com._data._data.community.entity.Follow;
import com._data._data.community.entity.Like;
import com._data._data.community.entity.Post;
import com._data._data.game.entity.UserGameInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import javax.xml.stream.events.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private Long nations;

    private String translationLang;

    private String password;

    private String profileImage;

    // --- 연관관계 ---
    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<Like> likes;

    // 내가 팔로우 하고 있는 사람
    @OneToMany(mappedBy = "follower")
    private List<Follow> following;

    // 나를 팔로우 하는 사람
    @OneToMany(mappedBy = "followee")
    private List<Follow> followers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserGameInfo gameInfo;
}
