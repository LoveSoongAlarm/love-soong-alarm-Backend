package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
