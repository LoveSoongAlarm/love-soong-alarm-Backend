//package com.lovesoongalarm.lovesoongalarm.domain.location.implement;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ZSetOperations;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.*;
//
//@Slf4j
//@Component
//@EnableScheduling
//@RequiredArgsConstructor
//public class SweepUserLocationScheduling {
//    private final StringRedisTemplate stringRedisTemplate;
//    private final RedisPipeline redisPipeline;
//
//    private static List<String> getStrings(List<Object> lastSeens, List<String> users, long cutoff) {
//        List<String> expired = new ArrayList<>();
//        for (int i = 0; i < lastSeens.size(); i++) {
//            String userId = users.get(i);
//            String ts = (String) lastSeens.get(i);
//            if (ts == null) {
//                expired.add(userId);
//                continue;
//            }
//            try {
//                long t = Long.parseLong(ts);
//                if (t <= cutoff) expired.add(userId);
//            } catch (NumberFormatException e) {
//                expired.add(userId);
//            }
//        }
//        return expired;
//    }
//
//    @Scheduled(cron = "0 0/30 * * * *")
//    public void sweepExpired() {
//        stringRedisTemplate.opsForValue().set(LAST_SEEN_KEY + "29", String.valueOf(Instant.now().getEpochSecond()));
//        stringRedisTemplate.opsForZSet().add(LAST_SEEN_INDEX_KEY, "29", Instant.now().getEpochSecond());
//
//        stringRedisTemplate.opsForValue().set(LAST_SEEN_KEY + "30", String.valueOf(Instant.now().getEpochSecond()));
//        stringRedisTemplate.opsForZSet().add(LAST_SEEN_INDEX_KEY, "30", Instant.now().getEpochSecond());
//
//        long cutoff = Instant.now().getEpochSecond() - 10800;
//
//        while (true) {
//            Set<ZSetOperations.TypedTuple<String>> batch =
//                    stringRedisTemplate.opsForZSet().rangeByScoreWithScores(LAST_SEEN_INDEX_KEY,
//                            Double.NEGATIVE_INFINITY, cutoff, 0, 500);
//            if (batch == null || batch.isEmpty()) {
//                break;
//            }
//
//            List<String> users = batch.stream().map(ZSetOperations.TypedTuple::getValue).toList();
//
//            List<Object> lastSeens = redisPipeline.pipe(ops -> {
//                for (String user : users) {
//                    ops.opsForValue().get(LAST_SEEN_KEY + user);
//                }
//            });
//
//            Set<String> excludeIds = Set.of("29", "30");
//
//            List<String> expired = getStrings(lastSeens, users, cutoff)
//                    .stream()
//                    .filter(id -> !excludeIds.contains(id))
//                    .toList();
//            if (expired.isEmpty()) {
//                if (batch.size() < 500) break;
//                continue;
//            }
//
//            List<Object> zones = redisPipeline.pipe(ops -> {
//                for (String userId : expired) {
//                    ops.opsForValue().get(ZONE_KEY + userId);
//                }
//            });
//
//            redisPipeline.pipe(ops -> {
//                for (int i = 0; i < expired.size(); i++) {
//                    String userId = expired.get(i);
//                    String zone = (String) zones.get(i);
//
//                    if (zone != null && !zone.isBlank()) {
//                        ops.opsForGeo().remove(GEO_KEY + zone, userId);
//                    }
//
//                    ops.delete(ZONE_KEY + userId);
//                    ops.delete(LAST_SEEN_KEY + userId);
//                    ops.opsForZSet().remove(LAST_SEEN_INDEX_KEY, userId);
//                }
//            });
//
//            if (batch.size() < 500) break;
//        }
//    }
//}
