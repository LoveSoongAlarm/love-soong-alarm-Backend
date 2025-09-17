package com.lovesoongalarm.lovesoongalarm.domain.notice.implement;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeSaver {
    private final NoticeRepository noticeRepository;

    public void save(Notice notice) {
        noticeRepository.save(notice);
    }
}
