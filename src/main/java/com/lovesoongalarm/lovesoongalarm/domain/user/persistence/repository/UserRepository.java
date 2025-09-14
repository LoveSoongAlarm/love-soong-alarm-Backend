package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // @Query
    @Query("select u.id as id, u.role as role, u.status as status from User u where u.serialId = :serialId")
    Optional<UserSecurityForm> findUserSecurityFromBySerialId(@Param("serialId") String serialId);

    @Query("select u.id as id, u.role as role, u.status as status from User u where u.id = :id")
    Optional<UserSecurityForm> findUserSecurityFromById(@Param("id") Long id);

    // query method
    Optional<User> findBySerialId(String serialId);
    Optional<User> findById(Long id);

    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.interests i
            LEFT JOIN FETCH i.hashtags h
            WHERE u.id = (
                SELECT cp.user.id 
                FROM ChatRoomParticipant cp 
                WHERE cp.chatRoom.id = :chatRoomId 
                AND cp.user.id != :userId
            )
            """)
    User findPartnerByChatRoomIdAndUserId(Long roomId, Long userId);
}
