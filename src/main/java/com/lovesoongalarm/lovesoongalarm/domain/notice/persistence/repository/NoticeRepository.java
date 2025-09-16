package com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
