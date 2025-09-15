package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.filter.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.chat.sub.filter.persistence.entity.ContentFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentFilterRepository extends JpaRepository<ContentFilter, Long> {
}
