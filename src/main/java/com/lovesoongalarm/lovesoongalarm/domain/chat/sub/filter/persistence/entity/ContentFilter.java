package com.lovesoongalarm.lovesoongalarm.domain.chat.sub.filter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "content_filters")
@NoArgsConstructor
public class ContentFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keyword", nullable = false, unique = true, length = 50)
    private String keyword;
}
