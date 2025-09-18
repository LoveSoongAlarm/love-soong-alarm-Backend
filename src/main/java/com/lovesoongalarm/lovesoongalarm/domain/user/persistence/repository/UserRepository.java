package com.lovesoongalarm.lovesoongalarm.domain.user.persistence.repository;

import com.lovesoongalarm.lovesoongalarm.domain.user.persistence.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // @Query
    @Query("select u.id as id, u.role as role, u.status as status from User u where u.serialId = :serialId")
    Optional<UserSecurityForm> findUserSecurityFromBySerialId(@Param("serialId") String serialId);

    @Query("select u.id as id, u.role as role, u.status as status from User u where u.id = :id")
    Optional<UserSecurityForm> findUserSecurityFromById(@Param("id") Long id);

    Optional<User> findById(Long id);

    @Query("""
            SELECT DISTINCT u FROM User u
            JOIN FETCH u.interests i
            WHERE u.id = (
                SELECT cp.user.id 
                FROM ChatRoomParticipant cp 
                WHERE cp.chatRoom.id = :roomId
                AND cp.user.id != :userId
            )
            """)
    User findPartnerByChatRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Modifying
    @Query("""
            UPDATE User u 
            SET u.remainingSlot = u.remainingSlot - 1 
            WHERE u.id = :userId 
            AND u.remainingSlot > 0
            """)
    void decreaseRemainingSlot(@Param("userId") Long userId);
}
