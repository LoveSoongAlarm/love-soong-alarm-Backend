package com.lovesoongalarm.lovesoongalarm.domain.location.application.facade;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.MatchingResultDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyUserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.business.LocationService;
import com.lovesoongalarm.lovesoongalarm.domain.location.implement.RedisPipeline;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationFacade {
    private final LocationService locationService;
    private final UserQueryService userQueryService;
    private final RedisPipeline redisPipeline;

    @Transactional
    public void updateLocation(Long userId, double latitude, double longitude) {
        locationService.updateLocation(userId, latitude, longitude);
    }

    @Transactional
    public NearbyResponseDTO findNearby(Long userId) {
        try {
            List<NearbyUserResponseDTO> nearbyUserResponse = new ArrayList<>();
            MatchingResultDTO matchingResult = locationService.findNearby(userId);

            log.info("matchingResult: {}", matchingResult);

            List<Object> pipeLastSeenResults = redisPipeline.pipe(ops -> {
                for (Long id : matchingResult.userIds()) {
                    ops.opsForValue().get("lastseen:" + id);
                }
            });

            int i = 0;
            for (Long id : matchingResult.userIds()) {
                UserResponseDTO user = userQueryService.getUser(id);
                log.info("userResponseDTO: {}", user);

                String lastSeenStr = (String) pipeLastSeenResults.get(i++);
                long lastSeen = (lastSeenStr != null) ? Long.parseLong(lastSeenStr) : 0L;

                String time = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(lastSeen),
                        ZoneId.of("Asia/Seoul")
                ).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                log.info("{} lastSeen: {}", id, time);

                nearbyUserResponse.add(NearbyUserResponseDTO.builder()
                        .name(user.name())
                        .age(user.age())
                        .major(user.major())
                        .lastSeen(time)
                        .emoji(user.emoji())
                        .interests(user.interests())
                        .matchCount(matchingResult.userMatchCounts().getOrDefault(id, 0L))
                        .build());
            }

            log.info("nearbyUserResponse: {}", nearbyUserResponse);

            return NearbyResponseDTO.builder()
                    .matchCount(matchingResult.matchCount())
                    .nearbyUsersInformation(nearbyUserResponse)
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("findNearby() failed. userId={}", userId, e);
            throw new CustomException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
