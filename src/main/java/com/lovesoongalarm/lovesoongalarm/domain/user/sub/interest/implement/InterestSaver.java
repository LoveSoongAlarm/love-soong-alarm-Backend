package com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.implement;

import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.entity.Interest;
import com.lovesoongalarm.lovesoongalarm.domain.user.sub.interest.persistence.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InterestSaver {

    private final InterestRepository interestRepository;

    public void saveAll(List<Interest> interests){
        interestRepository.saveAll(interests);
    }
}
