package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInterestResponseDTO(
        String detailLabel,
        List<String> hashTags
) {
    public static UserInterestResponseDTO from(Interest interest){
        return UserInterestResponseDTO.builder()
                .detailLabel(interest.getDetailLabel().getValue())
                .hashTags(interest.getHashtags().stream().map(
                        hashtag -> hashtag.getLabel()
                    ).toList()
                )
                .build();
    }
}
