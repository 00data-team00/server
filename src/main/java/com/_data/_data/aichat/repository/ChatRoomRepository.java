package com._data._data.aichat.repository;

import com._data._data.aichat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByTopicId(Long topicId);

    Optional<ChatRoom> findChatRoomByTitle(String title);

    List<ChatRoom> findByTopicIdOrderByUpdatedAtDesc(Long topicId);

    List<ChatRoom> findByUserIdOrderByUpdatedAtDesc(Long userId);

    void deleteChatRoomByUserId(Long userId);

    void deleteChatRoomById(Long id);

    // 벌크 삭제 메서드 - 성능 최적화
    @Transactional
    @Modifying
    @Query("DELETE FROM ChatRoom c WHERE c.id IN :ids")
    int deleteAllByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT cr From ChatRoom cr WHERE cr.userId = :userId AND " +
        "(SELECT COUNT(m) FROM Message m WHERE m.chatRoomId = cr.id) = :messageCount")
    List<ChatRoom> findByUserIdAndMessageCount(@Param("userId") Long userId, @Param("messageCount") int messageCount);
}
