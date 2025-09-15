package com.lovesoongalarm.lovesoongalarm.domain.user.implement;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeleter {

    private final UserRepository userRepository;

    public void deleteUser(User user){
        userRepository.delete(user);
    }
}
