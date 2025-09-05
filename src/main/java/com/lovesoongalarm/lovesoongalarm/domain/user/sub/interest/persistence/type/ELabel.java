package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ELabel {
    MUSIC("음악"),
    MEDIA("미디어"),
    GAME("게임"),
    EXERCISE("운동"),
    SPORTS("스포츠"),
    READING("독서"),
    FASHION("패션"),
    EATING("식도락"),
    TRAVELING("여행");

    private final String value;

}
