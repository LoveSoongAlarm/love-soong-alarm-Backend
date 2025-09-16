package com.lovesoongalarm.lovesoongalarm.domain.location.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.MatchingResultDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.business.LocationService;
import com.lovesoongalarm.lovesoongalarm.domain.location.exception.LocationErrorCode;
import com.lovesoongalarm.lovesoongalarm.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisPipeline redisPipeline;
    private final ZoneResolver zoneResolver;

    private static List<String> getStrings(List<Object> lastSeens, List<String> users, long cutoff) {
        List<String> expired = new ArrayList<>();
        for (int i = 0; i < lastSeens.size(); i++) {
            String userId = users.get(i);
            String ts = (String) lastSeens.get(i);
            if (ts == null) {
                expired.add(userId);
                continue;
            }
            try {
                long t = Long.parseLong(ts);
                if (t <= cutoff) expired.add(userId);
            } catch (NumberFormatException e) {
                expired.add(userId);
            }
        }
        return expired;
    }

    @Override
    @Transactional
    public void updateLocation(Long userId, double latitude, double longitude) {
        String newZone = zoneResolver.resolve(latitude, longitude);
        log.info("user new zone : {}", newZone);
        if (newZone == null || newZone.isBlank()) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }
        String stringUserId = String.valueOf(userId);
        String prevZone = stringRedisTemplate.opsForValue().get(ZONE_KEY + userId);
        log.info("user prev zone : {}", prevZone);
        Long nowSec = Instant.now().getEpochSecond();

        redisPipeline.pipe(ops -> {
            if (prevZone != null && !prevZone.equals(newZone)) {
                ops.opsForZSet().remove(GEO_KEY + prevZone, stringUserId);
            }

            ops.opsForGeo().add(
                    GEO_KEY + newZone,
                    new RedisGeoCommands.GeoLocation<>(stringUserId, new Point(longitude, latitude))
            );
            ops.opsForValue().set(ZONE_KEY + userId, newZone);
            ops.opsForValue().set(LAST_SEEN_KEY + stringUserId, String.valueOf(nowSec));
            ops.opsForZSet().add(LAST_SEEN_INDEX_KEY, stringUserId, nowSec);
        });
    }

    @Override
    @Transactional
    public MatchingResultDTO findNearby(Long userId) {
        String stringUserId = String.valueOf(userId);

        String zone = stringRedisTemplate.opsForValue().get(ZONE_KEY + userId);
        if (zone == null || zone.isBlank()) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }
        log.info("user zone : {}", zone);

        var pos = stringRedisTemplate.opsForGeo().position(GEO_KEY + zone, stringUserId);
        if (pos == null || pos.isEmpty() || pos.get(0) == null) {
            throw new CustomException(LocationErrorCode.USER_GEO_NOT_FOUND);
        }
        log.info("user position : {}", pos);

        var res = stringRedisTemplate.opsForGeo().search(
                GEO_KEY + zone,
                GeoReference.fromMember(stringUserId),
                new Distance(50, RedisGeoCommands.DistanceUnit.METERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().sortAscending().limit(50)
        );
        log.info("user search : {}", res);

        if (res == null || res.getContent().isEmpty()) {
            return MatchingResultDTO.builder()
                    .matchCount(0)
                    .userIds(List.of())
                    .build();
        }

        return matching(userId, res.getContent().stream()
                .map(g -> Long.parseLong(g.getContent().getName()))
                .filter(id -> !id.equals(userId))
                .toList());
    }

    private MatchingResultDTO matching(Long userId, List<Long> nearbyUsers) {
        String myGender = stringRedisTemplate.opsForValue().get(USER_GENDER_KEY + userId);
        log.info("my gender : {}", myGender);
        if (myGender == null) {
            throw new CustomException(UserErrorCode.USER_GENDER_NOT_FOUND);
        }

        Set<String> myInterests = stringRedisTemplate.opsForSet().members(USER_INTEREST_KEY + userId);

        List<Object> pipeResults = redisPipeline.pipe(ops -> {
            for (Long id : nearbyUsers) {
                ops.opsForValue().get(USER_GENDER_KEY + id);
                ops.opsForSet().members(USER_INTEREST_KEY + id);
            }
        });

        List<Long> filteredUsers = new ArrayList<>();
        Map<Long, Set<String>> interestMap = new HashMap<>();

        for (int i = 0; i < nearbyUsers.size(); i++) {
            Long id = nearbyUsers.get(i);

            String gender = (String) pipeResults.get(i * 2);
            log.info("{} gender : {}", id, gender);
            Set<String> interests = (Set<String>) pipeResults.get(i * 2 + 1);

            if (gender == null || gender.equals(myGender)) {
                continue;
            }

            filteredUsers.add(id);
            interestMap.put(id, interests);
        }

        Collections.shuffle(filteredUsers);
        List<Long> randomNearbyUsers = filteredUsers.subList(0, Math.min(6, filteredUsers.size()));

        int matchCount = 0;
        Map<Long, Long> userMatchCounts = new HashMap<>();

        for (Long id : randomNearbyUsers) {
            Set<String> interests = interestMap.get(id);
            long overlap = (interests == null) ? 0 : interests.stream().filter(myInterests::contains).count();

            userMatchCounts.put(id, overlap);

            if (overlap == 2) {
                matchCount++;
            }
        }

        log.info("randomNearbyUsers : {}", randomNearbyUsers);

        return MatchingResultDTO.builder()
                .matchCount(matchCount)
                .userIds(randomNearbyUsers)
                .userMatchCounts(userMatchCounts)
                .build();
    }

    @Scheduled(fixedDelay = 60_000)
    public void sweepExpired() {
        long cutoff = Instant.now().getEpochSecond() - 600;

        while (true) {
            Set<ZSetOperations.TypedTuple<String>> batch =
                    stringRedisTemplate.opsForZSet().rangeByScoreWithScores(LAST_SEEN_INDEX_KEY,
                            Double.NEGATIVE_INFINITY, cutoff, 0, 500);
            if (batch == null || batch.isEmpty()) {
                break;
            }

            List<String> users = batch.stream().map(ZSetOperations.TypedTuple::getValue).toList();

            List<Object> lastSeens = redisPipeline.pipe(ops -> {
                for (String user : users) {
                    ops.opsForValue().get(LAST_SEEN_KEY + user);
                }
            });

            List<String> expired = getStrings(lastSeens, users, cutoff);
            if (expired.isEmpty()) {
                if (batch.size() < 500) break;
                continue;
            }

            List<Object> zones = redisPipeline.pipe(ops -> {
                for (String userId : expired) {
                    ops.opsForValue().get(ZONE_KEY + userId);
                }
            });

            redisPipeline.pipe(ops -> {
                for (int i = 0; i < expired.size(); i++) {
                    String userId = expired.get(i);
                    String zone = (String) zones.get(i);

                    if (zone != null && !zone.isBlank()) {
                        ops.opsForGeo().remove(GEO_KEY + zone, userId);
                    }

                    ops.delete(ZONE_KEY + userId);
                    ops.delete(LAST_SEEN_KEY + userId);
                    ops.opsForZSet().remove(LAST_SEEN_INDEX_KEY, userId);
                }
            });

            if (batch.size() < 500) break;
        }
    }
}
