package com.lovesoongalarm.lovesoongalarm.domain.location.application.facade;

import com.lovesoongalarm.lovesoongalarm.common.code.GlobalErrorCode;
import com.lovesoongalarm.lovesoongalarm.common.exception.CustomException;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.MatchingResultDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.application.dto.NearbyUserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.location.business.LocationService;
import com.lovesoongalarm.lovesoongalarm.domain.location.implement.RedisPipeline;
import com.lovesoongalarm.lovesoongalarm.domain.notification.business.NotificationQueryService;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationBadgeUpdateEvent;
import com.lovesoongalarm.lovesoongalarm.domain.notification.event.NotificationCreatedEvent;
import com.lovesoongalarm.lovesoongalarm.domain.user.application.dto.UserResponseDTO;
import com.lovesoongalarm.lovesoongalarm.domain.user.business.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.GEO_KEY;
import static com.lovesoongalarm.lovesoongalarm.common.constant.RedisKey.LAST_SEEN_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationFacade {
    private final LocationService locationService;
    private final UserQueryService userQueryService;
    private final RedisPipeline redisPipeline;
    private final NotificationQueryService notificationQueryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void updateLocation(Long userId, double latitude, double longitude) {
        locationService.updateLocation(userId, latitude, longitude);
    }

    public NearbyResponseDTO findNearby(Long userId) {
        try {
            List<NearbyUserResponseDTO> nearbyUserResponse = new ArrayList<>();
            MatchingResultDTO matchingResult = locationService.findNearby(userId);

            log.info("matchingResult: {}", matchingResult);

            List<Object> pipeResults = redisPipeline.pipe(ops -> {
                for (MatchingResultDTO.NearbyUserMatchDTO userMatch : matchingResult.nearbyUsers()) {
                    ops.opsForGeo().position(GEO_KEY + matchingResult.zone(), String.valueOf(userMatch.userId()));

                }
            });

            int i = 0;

            List<Long> userIds = matchingResult.nearbyUsers().stream()
                    .map(MatchingResultDTO.NearbyUserMatchDTO::userId)
                    .toList();

            Map<Long, UserResponseDTO> userMap = userQueryService.getAllUser(userIds).stream()
                    .collect(Collectors.toMap(UserResponseDTO::id, Function.identity()));

            List<NotificationCreatedEvent.NotificationHolder> holders = new ArrayList<>();
            for (MatchingResultDTO.NearbyUserMatchDTO userMatch : matchingResult.nearbyUsers()) {
                UserResponseDTO user = userMap.get(userMatch.userId());

                log.info("userResponseDTO: {}", user);

                List<Point> posList = (List<Point>) pipeResults.get(i++);
                Double latitude = null;
                Double longitude = null;
                if (posList != null && !posList.isEmpty() && posList.get(0) != null) {
                    longitude = posList.get(0).getX();
                    latitude = posList.get(0).getY();
                }
                log.debug("{} lat: {}, lon: {}", userMatch.userId(), latitude, longitude);

                nearbyUserResponse.add(NearbyUserResponseDTO.builder()
                        .userId(userMatch.userId())
                        .name(user.name())
                        .age(user.age())
                        .major(user.major())
                        .lastSeen(user.lastSeen())
                        .emoji(user.emoji())
                        .interests(user.interests())
                        .isMatching(userMatch.isMatching())
                        .latitude(latitude)
                        .longitude(longitude)
                        .build());

                if (userMatch.isMatching()) {
                    try {
                        Optional.ofNullable(notificationQueryService.saveNotification(userId, userMatch.userId(), new ArrayList<>(userMatch.overlapInterests())))
                                .ifPresent(notification -> holders.add(new NotificationCreatedEvent.NotificationHolder(userId, notification)));

                        Optional.ofNullable(notificationQueryService.saveNotification(userMatch.userId(), userId, new ArrayList<>(userMatch.overlapInterests())))
                                .ifPresent(notification -> holders.add(new NotificationCreatedEvent.NotificationHolder(userMatch.userId(), notification)));
                    } catch (UnexpectedRollbackException e) {
                        log.error("findNearby() failed. userId={}", userId, e);
                    }
                }
            }

            log.info("nearbyUserResponse: {}", nearbyUserResponse);

            if (!holders.isEmpty()) {
                applicationEventPublisher.publishEvent(new NotificationCreatedEvent(holders));

                applicationEventPublisher.publishEvent(
                        NotificationBadgeUpdateEvent.builder()
                                .userId(userId)
                                .hasUnRead(true)
                                .build()
                );
            }

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
