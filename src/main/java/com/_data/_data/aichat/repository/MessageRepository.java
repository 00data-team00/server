package com._data._data.aichat.repository;

import com._data._data.aichat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoomIdOrderByStoredAt(Long chatRoomId);
}
