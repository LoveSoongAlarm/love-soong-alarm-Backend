package com.lovesoongalarm.lovesoongalarm.domain.notice.business;

import com.lovesoongalarm.lovesoongalarm.domain.notice.application.converter.NoticeConverter;
import com.lovesoongalarm.lovesoongalarm.domain.notice.application.dto.NoticeResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notice.implement.NoticeRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notice.implement.NoticeSaver;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.type.ENoticeStatus;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {
    private final UserRetriever userRetriever;
    private final NoticeRetriever noticeRetriever;
    private final NoticeConverter noticeConverter;
    private final NoticeSaver noticeSaver;

    @Transactional
    public List<NoticeResponseDTO> notice(Long userId) {
        return noticeRetriever.findNoticesByUserId(userId).stream()
                .map(noticeConverter::toNoticeResponseDTO)
                .toList();
    }

    @Transactional
    public void sendNotice(Long userId, Long matchingUserId, List<String> interests) {
        User user = userRetriever.findById(userId);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String interestTags = interests.stream()
                .map(EDetailLabel::valueOf)
                .map(EDetailLabel::getValue)
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));

        String message = String.format("내 주변 50m에 %s를 좋아하는 %s이 있어요!", interestTags, user.getGender().getValue());

        Notice notice = Notice.create(user, matchingUserId, message, ENoticeStatus.NOT_READ, now);

        noticeSaver.save(notice);
    }
}
