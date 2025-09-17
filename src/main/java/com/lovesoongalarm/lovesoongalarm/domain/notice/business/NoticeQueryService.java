package com.lovesoongalarm.lovesoongalarm.domain.notice.business;

import com.lovesoongalarm.lovesoongalarm.domain.notice.application.converter.NoticeConverter;
import com.lovesoongalarm.lovesoongalarm.domain.notice.application.dto.NoticeResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.notice.implement.NoticeRetriever;
import com.lovesoongalarm.lovesoongalarm.domain.notice.implement.NoticeSaver;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type.EDetailLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {
    private final NoticeRetriever noticeRetriever;
    private final NoticeConverter noticeConverter;

    public List<NoticeResponseDTO> notice(Long userId) {
        return noticeRetriever.findNoticesByUserId(userId).stream()
                .map(noticeConverter::toNoticeResponseDTO)
                .toList();
    }
}
