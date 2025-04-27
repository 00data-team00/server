package com._data._data.aichat.repository;

import com._data._data.aichat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByTopicId(Long topicId);

    Optional<ChatRoom> findChatRoomByTitle(String title);

    List<ChatRoom> findByTopicIdOrderByUpdatedAtDesc(Long topicId);

    List<ChatRoom> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
