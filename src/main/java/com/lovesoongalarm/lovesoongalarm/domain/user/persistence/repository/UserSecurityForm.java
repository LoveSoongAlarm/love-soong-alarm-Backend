package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.ERole;
import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.type.EUserStatus;

public interface UserSecurityForm {
    Long getId();
    ERole getRole();
    EUserStatus getStatus();

    static UserSecurityForm invoke(User user){
        return new UserSecurityForm() {
            @Override
            public Long getId() {
                return user.getId();
            }

            @Override
            public ERole getRole() {
                return user.getRole();
            }

            @Override
            public EUserStatus getStatus() {
                return user.getStatus();
            }


        };
    }
}
