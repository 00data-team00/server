package com._data._data.aichat.service;

import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.ChatRoomRepository;
import com._data._data.aichat.repository.MessageRepository;
import com._data._data.aichat.repository.TopicRepository;
import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final TopicRepository topicRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChatRoom creatChatRoom(Long topicId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailAndIsDeletedFalse(userDetails.getUsername());

        Long userId = user.getId();

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        ChatRoom chatRoom = ChatRoom.builder()
                .userId(userId)
                .topicId(topicId)
                .title(topic.getTitle())
                .description(topic.getDescription())
                .isFinished(false)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> getChatRooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailAndIsDeletedFalse(userDetails.getUsername());

        List<ChatRoom> allChatRooms = chatRoomRepository.findByUserIdOrderByUpdatedAtDesc(user.getId());

        if (allChatRooms.isEmpty()) {
            return allChatRooms;
        }

        List<Long> chatRoomIds = allChatRooms.stream()
                .map(ChatRoom::getId)
                .toList();

        Map<Long, Integer> messageCountMap = getMessageCountMap(chatRoomIds);

        List<ChatRoom> filteredChatRooms = allChatRooms.stream()
                .filter(chatRoom -> {
                    Integer messageCount = messageCountMap.getOrDefault(chatRoom.getId(), 0);

                    return messageCount >= 2;
                })
                .toList();

        List<Long> toDeleteChatRoomIds = allChatRooms.stream()
                .filter(chatRoom -> {
                    Integer messageCount = messageCountMap.getOrDefault(chatRoom.getId(), 0);

                    return messageCount == 1;
                })
                .map(ChatRoom::getId)
                .toList();

        if (!toDeleteChatRoomIds.isEmpty()) {
            log.info("비동기 삭제 대상 채팅방 개수: {}", toDeleteChatRoomIds.size());
            deleteChatRoomsAsync(toDeleteChatRoomIds);
        }

        return filteredChatRooms;
    }

    private Map<Long, Integer> getMessageCountMap(List<Long> chatRoomIds) {
        if (chatRoomIds == null || chatRoomIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<Object[]> results = messageRepository.countMessagesByChatRoomIds(chatRoomIds);

            return results.stream()
                    .filter(Objects::nonNull)  // null 체크 추가
                    .collect(Collectors.toMap(
                            result -> (Long) result[0],
                            result -> ((Number) result[1]).intValue(),  // 안전한 형변환
                            (existing, replacement) -> existing  // 중복 키 처리
                    ));
        } catch (Exception e) {
            log.error("메시지 개수 조회 중 오류 발생", e);
            return new HashMap<>();
        }
    }

    @Async("chatRoomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> deleteChatRoomsAsync(List<Long> chatRoomIds) {
        log.info("비동기 삭제 시작 - 채팅방 ID: {}", chatRoomIds);

        try {
            int batchSize = 50;

            for (int i = 0; i < chatRoomIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, chatRoomIds.size());
                List<Long> batchIds = chatRoomIds.subList(i, endIndex);

                deleteChatRoomBatch(batchIds);

                if (endIndex < chatRoomIds.size()) {
                    Thread.sleep(50);
                }
            }

            log.info("비동기 삭제 완료 - 총 {}개 채팅방 삭제", chatRoomIds.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("비동기 삭제 중 인터럽트 발생", e);
            throw new RuntimeException("채팅방 삭제가 중단되었습니다", e);
        } catch (Exception e) {
            log.error("비동기 삭제 중 오류 발생", e);
            throw new RuntimeException("채팅방 삭제 중 오류가 발생했습니다", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteChatRoomBatch(List<Long> chatRoomIds) {
        try {
            // 1. 벌크 삭제로 성능 향상
            int deletedMessages = messageRepository.deleteAllByChatRoomIdIn(chatRoomIds);
            log.debug("삭제된 메시지 수: {}", deletedMessages);

            int deletedChatRooms = chatRoomRepository.deleteAllByIdIn(chatRoomIds);
            log.debug("삭제된 채팅방 수: {}", deletedChatRooms);

            log.debug("배치 삭제 완료 - 채팅방 ID: {}", chatRoomIds);
        } catch (Exception e) {
            log.error("배치 삭제 중 오류 발생 - 채팅방 ID: {}", chatRoomIds, e);
            throw e;
        }
    }
}
