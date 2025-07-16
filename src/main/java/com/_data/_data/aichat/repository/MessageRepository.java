package com._data._data.aichat.repository;

import com._data._data.aichat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoomIdOrderByStoredAt(Long chatRoomId);

    List<Message> findTop10ByChatRoomIdOrderByStoredAtDesc(Long chatRoomId);

    default List<Message> findRecent10ByChatRoomIdInChronologicalOrder(Long chatRoomId) {
        // 1) 최신순 10개를 먼저 가져옴
        List<Message> recentMessages = findTop10ByChatRoomIdOrderByStoredAtDesc(chatRoomId);
        // 2) 시간순으로 재정렬
        recentMessages.sort(Comparator.comparing(Message::getStoredAt));
        return recentMessages;
    }

    int countByChatRoomId(Long chatRoomId);

    @Query("SELECT m.chatRoomId, COUNT(m) FROM Message m WHERE m.chatRoomId IN :chatRoomIds GROUP BY m.chatRoomId")
    List<Object[]> countMessagesByChatRoomIds(@Param("chatRoomIds") List<Long> chatRoomIds);

    // 벌크 삭제 메서드 - 성능 최적화
    @Transactional
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chatRoomId IN :chatRoomIds")
    int deleteAllByChatRoomIdIn(@Param("chatRoomIds") List<Long> chatRoomIds);
}
