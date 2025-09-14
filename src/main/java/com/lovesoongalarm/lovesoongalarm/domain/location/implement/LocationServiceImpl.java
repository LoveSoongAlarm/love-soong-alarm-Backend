package com.lovesoongalarm.lovesoongalarm.domain.location.implement;

import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.location.business.LocationService;
import com.lovesoongalarm.lovesoongalarm.domain.location.exception.LocationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final StringRedisTemplate stringRedisTemplate;
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

    private String geoKey(String zoneId) {
        return "location:zone:" + zoneId;
    }

    private String userZoneKey(Long userId) {
        return "user:zone:" + userId;
    }

    private String lastSeenKey(String userId) {
        return "lastseen:" + userId;
    }

    private String lastSeenIndexKey() {
        return "lastseen:index";
    }

    private List<Object> pipe(java.util.function.Consumer<RedisOperations<String, String>> block) {
        SessionCallback<Object> cb = new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) throws DataAccessException {
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                block.accept(ops);
                return null;
            }
        };
        return stringRedisTemplate.executePipelined(cb);
    }

    @Override
    public void updateLocation(Long userId, double latitude, double longitude) {
        String newZone = zoneResolver.resolve(latitude, longitude);
        if (newZone == null || newZone.isBlank()) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }
        String stringUserId = String.valueOf(userId);
        String prevZone = stringRedisTemplate.opsForValue().get(userZoneKey(userId));
        Long nowSec = Instant.now().getEpochSecond();

        pipe(ops -> {
            if (prevZone != null && !prevZone.equals(newZone)) {
                ops.opsForZSet().remove(geoKey(prevZone), stringUserId);
            }

            ops.opsForGeo().add(
                    geoKey(newZone),
                    new RedisGeoCommands.GeoLocation<>(stringUserId, new Point(longitude, latitude))
            );
            ops.opsForValue().set(userZoneKey(userId), newZone);
            ops.opsForValue().set(lastSeenKey(stringUserId), String.valueOf(nowSec));
            ops.opsForZSet().add(lastSeenIndexKey(), stringUserId, nowSec);
        });
    }

    @Override
    public List<Long> findNearby(Long userId) {
        String stringUserId = String.valueOf(userId);

        String zone = stringRedisTemplate.opsForValue().get(userZoneKey(userId));
        if (zone == null || zone.isBlank()) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }

        var pos = stringRedisTemplate.opsForGeo().position(geoKey(zone), stringUserId);
        if (pos == null || pos.isEmpty() || pos.get(0) == null) {
            throw new CustomException(LocationErrorCode.USER_GEO_NOT_FOUND);
        }

        var res = stringRedisTemplate.opsForGeo().search(
                geoKey(zone),
                GeoReference.fromMember(stringUserId),
                new Distance(20, RedisGeoCommands.DistanceUnit.METERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().sortAscending().limit(50)
        );

        if (res == null) {
            return List.of();
        } else {
            return res.getContent().stream()
                    .map(g -> Long.parseLong(g.getContent().getName()))
                    .filter(id -> !id.equals(userId))
                    .toList();
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void sweepExpired() {
        long cutoff = Instant.now().getEpochSecond() - 600;

        while (true) {
            Set<ZSetOperations.TypedTuple<String>> batch =
                    stringRedisTemplate.opsForZSet().rangeByScoreWithScores(lastSeenIndexKey(),
                            Double.NEGATIVE_INFINITY, cutoff, 0, 500);
            if (batch == null || batch.isEmpty()) {
                break;
            }

            List<String> users = batch.stream().map(ZSetOperations.TypedTuple::getValue).toList();

            List<Object> lastSeens = pipe(ops -> {
                for (String user : users) {
                    ops.opsForValue().get(lastSeenKey(user));
                }
            });

            List<String> expired = getStrings(lastSeens, users, cutoff);
            if (expired.isEmpty()) {
                if (batch.size() < 500) break;
                continue;
            }

            List<Object> zones = pipe(ops -> {
                for (String userId : expired) {
                    ops.opsForValue().get(userZoneKey(Long.valueOf(userId)));
                }
            });

            pipe(ops -> {
                for (int i = 0; i < expired.size(); i++) {
                    String userId = expired.get(i);
                    String zone = (String) zones.get(i);

                    if (zone != null && !zone.isBlank()) {
                        ops.opsForGeo().remove(geoKey(zone), userId);
                    }

                    ops.delete(userZoneKey(Long.valueOf(userId)));
                    ops.delete(lastSeenKey(userId));
                    ops.opsForZSet().remove(lastSeenIndexKey(), userId);
                }
            });

            if (batch.size() < 500) break;
        }
    }
}
