package com.lovesoongalarm.lovesoongalarm.domain.user.application.dto;

import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import lombok.Builder;

import java.util.List;

@Builder
public record UserMeInterestDTO(
        String label,
        String detailLabel,
        List<String> hashTags
) {
    public static UserMeInterestDTO from(Interest interest){
        return UserMeInterestDTO.builder()
                .detailLabel(interest.getDetailLabel().getValue())
                .label(interest.getLabel().getValue())
                .hashTags(interest.getHashtags().stream().map(
                                hashtag -> hashtag.getLabel()
                        ).toList()
                )
                .build();
    }
}