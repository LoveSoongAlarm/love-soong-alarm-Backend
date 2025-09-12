package com.lovesoongalarm.lovesoongalarm.domain.user.business;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.implement.UserRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRetriever userRetriever;

    public String getUserNickname(Long userId) {
        User user = userRetriever.findByUserId(userId);
        return user.getNickname();
    }
}
