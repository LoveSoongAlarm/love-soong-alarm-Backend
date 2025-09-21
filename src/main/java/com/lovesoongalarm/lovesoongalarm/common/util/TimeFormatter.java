package com.lovesoongalarm.lovesoongalarm.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    public static String formatTimeAgo(String lastSeen) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createdAt = LocalDateTime.parse(lastSeen, formatter);

        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (seconds < 60) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days <= 6) {
            return days + "일 전";
        } else {
            return createdAt.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        }
    }
}
