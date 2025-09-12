package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EDetailLabel {
    // 음악 (Music)
    BAND("밴드"),
    HIPHOP("힙합"),
    KPOP("케이팝"),
    CLASSICAL("클래식"),

    // 미디어 (Media)
    MOVIE("영화"),
    DRAMA("드라마"),
    ENTERTAINMENT("예능"),
    DOCUMENTARY("다큐멘터리"),

    // 게임 (Game)
    MOBILE_GAME("모바일게임"),
    CONSOLE_GAME("콘솔게임"),
    PC_GAME("PC게임"),

    // 운동 (Exercise)
    RUNNING("러닝"),
    FITNESS("헬스"),
    CLIMBING("클라이밍"),
    BOXING("복싱"),
    SWIMMING("수영"),
    BOARD("보드"),
    GYMNASTICS("체조"),
    DANCE("댄스"),

    // 스포츠 (Sports)
    KBO("KBO"),
    KLEAGUE("K리그"),
    OVERSEAS_SOCCER("해외축구"),
    ESPORTS("e스포츠"),
    BASKETBALL("농구"),
    VOLLEYBALL("배구"),
    MOTORSPORTS("모터스포츠"),

    // 독서 (Reading)
    NOVEL("소설"),
    ESSAY("에세이"),
    POETRY("시집"),
    WEBNOVEL("웹소설"),
    SELF_DEVELOPMENT("자기계발서"),

    // 패션 (Fashion)
    STREET("스트릿"),
    VINTAGE("빈티지"),
    FASHION_CLASSIC("클래식"),
    COLLECTING("수집"),

    // 식도락 (Food)
    RESTAURANT("맛집탐방"),
    CAFE("카페"),
    COOKING("요리"),

    // 여행 (Travel)
    DOMESTIC_TRAVEL("국내여행"),
    INTERNATIONAL_TRAVEL("해외여행"),
    CAMPING("캠핑");

    private final String value;
}

