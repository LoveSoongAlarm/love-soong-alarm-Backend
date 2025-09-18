package com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.notice.persistence.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUserId(Long userId);

    boolean existsByUserIdAndMatchingUserIdAndDate(Long userId, Long matchingUserId, LocalDate date);
}
