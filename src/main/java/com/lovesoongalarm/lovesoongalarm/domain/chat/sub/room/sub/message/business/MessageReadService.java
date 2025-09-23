package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.business;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.room.sub.message.implement.MessageUpdater;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserService;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReadService {

    private final MessageUpdater messageUpdater;
    private final UserService userService;

    @Transactional
    public ReadResult markUnreadMessagesAsRead(Long chatRoomId, Long userId) {
        log.info("미읽은 메시지 일괄 읽음 처리 시작 - chatRoomId: {}, userId: {}", chatRoomId, userId);

        try {
            User partner = userService.getPartnerUser(chatRoomId, userId);

            int updatedCount = messageUpdater.markMessagesAsReadByChatRoomAndReceiver(chatRoomId, partner.getId());
            if (updatedCount == 0) {
                log.info("읽음 처리할 메시지가 없음 - chatRoomId: {}, userId: {}", chatRoomId, userId);
                return ReadResult.empty();
            }

            log.info("미읽은 메시지 일괄 읽음 처리 완료 - updatedCount: {}, partnerId: {}",
                    updatedCount, partner.getId());
            return ReadResult.of(updatedCount, partner.getId(), chatRoomId, userId);
        } catch (Exception e) {
            log.error("미읽은 메시지 읽음 처리 중 오류 - chatRoomId: {}, userId: {}",
                    chatRoomId, userId, e);
            return ReadResult.empty();
        }
    }

    @Transactional
    public ReadResult markSingleMessageAsRead(Long messageId, Long chatRoomId, Long readerId) {
        log.info("단일 메시지 읽음 처리 - messageId: {}, readerId: {}", messageId, readerId);

        try {
            messageUpdater.markAsRead(messageId);

            User partner = userService.getPartnerUser(chatRoomId, readerId);

            log.info("단일 메시지 읽음 처리 완료 - messageId: {}, partnerId: {}",
                    messageId, partner.getId());
            return ReadResult.of(1, partner.getId(), chatRoomId, readerId);
        } catch (Exception e) {
            log.error("단일 메시지 읽음 처리 중 오류 - messageId: {}, readerId: {}",
                    messageId, readerId, e);
            return ReadResult.empty();
        }
    }


    public record ReadResult(
            int readCount,
            Long partnerId,
            Long chatRoomId,
            Long readerId,
            boolean hasUpdate
    ) {
        public static ReadResult empty() {
            return new ReadResult(0, null, null, null, false);
        }

        public static ReadResult of(int readCount, Long partnerId, Long chatRoomId, Long readerId) {
            return new ReadResult(readCount, partnerId, chatRoomId, readerId, true);
        }
    }
}
