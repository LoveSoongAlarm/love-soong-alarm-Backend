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
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisPipeline redisPipeline;
    private final ZoneResolver zoneResolver;

    @Override
    @Transactional
    public void updateLocation(Long userId, double latitude, double longitude) {
        String newZone = zoneResolver.resolve(latitude, longitude);
        log.info("user new zone : {}", newZone);
        if (newZone == null || newZone.isBlank()) {
            throw new CustomException(LocationErrorCode.USER_ZONE_NOT_FOUND);
        } else if (newZone.equals("8")) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }
        String stringUserId = String.valueOf(userId);
        String prevZone = stringRedisTemplate.opsForValue().get(ZONE_KEY + userId);
        log.info("user prev zone : {}", prevZone);
        Long nowSec = Instant.now().getEpochSecond();

        List<String> neighborZones = zoneResolver.getNeighborZones(newZone);

        List<String> targetZones = new ArrayList<>();
        targetZones.add(newZone);
        targetZones.addAll(neighborZones);

        redisPipeline.pipe(ops -> {
            if (prevZone != null && !prevZone.equals(newZone)) {
                List<String> prevTargetZones = new ArrayList<>();
                prevTargetZones.add(prevZone);
                prevTargetZones.addAll(zoneResolver.getNeighborZones(prevZone));

                for (String zone : prevTargetZones) {
                    ops.opsForZSet().remove(GEO_KEY + zone, stringUserId);
                }
            }

            for (String zone : targetZones) {
                ops.opsForGeo().add(
                        GEO_KEY + zone,
                        new RedisGeoCommands.GeoLocation<>(stringUserId, new Point(longitude, latitude))
                );
            }

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
            log.info("user zone : {}", zone);
            throw new CustomException(LocationErrorCode.USER_ZONE_NOT_FOUND);
        } else if (zone.equals("8")) {
            throw new CustomException(LocationErrorCode.OUT_OF_ZONE);
        }
        log.info("user zone : {}", zone);

        List<String> neighborZones = zoneResolver.getNeighborZones(zone);

        List<String> targetZones = new ArrayList<>();
        targetZones.add(zone);
        targetZones.addAll(neighborZones);

        var pos = stringRedisTemplate.opsForGeo().position(GEO_KEY + zone, stringUserId);
        if (pos == null || pos.isEmpty() || pos.get(0) == null) {
            throw new CustomException(LocationErrorCode.USER_GEO_NOT_FOUND);
        }
        log.info("user position : {}", pos);

        log.info("user neighbor zone : {}", targetZones);

        List<RedisGeoCommands.GeoLocation<String>> res = new ArrayList<>();

        for(String targetZone : targetZones) {
            var result = stringRedisTemplate.opsForGeo().search(
                    GEO_KEY + targetZone,
                    GeoReference.fromMember(stringUserId),
                    new Distance(100, RedisGeoCommands.DistanceUnit.METERS),
                    RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().sortAscending().limit(50)
            );
            log.info("user search : {}", result);
            if (result != null && !result.getContent().isEmpty()) {
                result.getContent().forEach(r -> res.add(r.getContent()));
            }
        }

        log.info("user search : {}", res);

        if(res.isEmpty()) {
            return MatchingResultDTO.builder()
                    .matchCount(0)
                    .zone(zone)
                    .nearbyUsers(List.of())
                    .build();
        }

        return matching(userId, zone, res.stream()
                .map(g -> Long.parseLong(g.getName()))
                .filter(id -> !id.equals(userId))
                .distinct()
                .toList());
    }

    private MatchingResultDTO matching(Long userId, String zone, List<Long> nearbyUsers) {
        String myGender = (String) stringRedisTemplate.opsForHash().get(USER_GENDER_KEY, String.valueOf(userId));
        log.info("my gender : {}", myGender);
        if (myGender == null) {
            throw new CustomException(UserErrorCode.USER_GENDER_NOT_FOUND);
        }

        Set<String> myInterests = stringRedisTemplate.opsForSet().members(USER_INTEREST_KEY + userId);

        if (myInterests == null) {
            myInterests = Collections.emptySet();
        }

        List<Object> genderPipeResult = redisPipeline.pipe(ops -> {
            for (Long id : nearbyUsers) {
                ops.opsForHash().get(USER_GENDER_KEY, String.valueOf(id));
            }
        });

        List<Long> filteredUsers = new ArrayList<>();

        for (int i = 0; i < nearbyUsers.size(); i++) {
            Long id = nearbyUsers.get(i);
            String gender = (String) genderPipeResult.get(i);
            if (gender != null && !gender.equals(myGender)) {
                filteredUsers.add(id);
            }
        }

        Collections.shuffle(filteredUsers);
        List<Long> randomNearbyUsers = filteredUsers.subList(0, Math.min(6, filteredUsers.size()));

        List<Object> interestPipeResults = redisPipeline.pipe(ops -> {
            for (Long id : randomNearbyUsers) {
                ops.opsForSet().members(USER_INTEREST_KEY + id);
            }
        });

        int matchCount = 0;

        List<MatchingResultDTO.NearbyUserMatchDTO> nearby = new ArrayList<>();

        for (int i = 0; i < randomNearbyUsers.size(); i++) {
            Long id = randomNearbyUsers.get(i);
            boolean isMatching = false;
            Set<String> interests = (Set<String>) interestPipeResults.get(i);

            Set<String> overlapInterests = (interests == null) ? Set.of() :
                    interests.stream()
                            .filter(myInterests::contains)
                            .collect(Collectors.toSet());

            long overlap = overlapInterests.size();

            if (overlap != 0) {
                matchCount++;
                isMatching = true;
            }

            nearby.add(MatchingResultDTO.NearbyUserMatchDTO.builder()
                    .userId(id)
                    .isMatching(isMatching)
                    .overlapInterests(overlapInterests)
                    .build());
        }

        log.info("randomNearbyUsers : {}", randomNearbyUsers);

        return MatchingResultDTO.builder()
                .matchCount(matchCount)
                .zone(zone)
                .nearbyUsers(nearby)
                .build();
    }
}
