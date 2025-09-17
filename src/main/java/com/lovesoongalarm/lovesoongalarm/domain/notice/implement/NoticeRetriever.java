package com.lovesoongalarm.lovesoongalarm.domain.notice.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeRetriever {
    private final NoticeRepository noticeRepository;

    public List<Notice> findNoticesByUserId(Long userId) {
        return noticeRepository.findByUserId(userId);
    }
}
