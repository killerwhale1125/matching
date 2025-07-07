package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.profile.infrastructure.entity.ProfileViewCountHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileViewCountHistoryJpaRepository extends JpaRepository<ProfileViewCountHistoryEntity, Long> {
    @Modifying
    @Query("UPDATE ProfileViewCountHistoryEntity ph SET ph.loss = ph.loss + :loss WHERE ph.profileId = :profileId")
    void markAsLossCount(@Param("profileId") Long profileId, @Param("loss") int loss);
}
