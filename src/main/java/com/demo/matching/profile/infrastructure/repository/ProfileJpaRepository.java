package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.profile.infrastructure.entity.ProfileEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    /* 프로필 & Member fetch join */
    @EntityGraph(attributePaths = {"member"})
    Optional<ProfileEntity> findWithMemberById(Long id);

    @Modifying
    @Query("UPDATE ProfileEntity p SET p.viewCount = p.viewCount + :viewCount WHERE p.id = :profileId")
    void incrementViewCountBy(@Param("profileId") Long profileId, @Param("viewCount") int viewCount);

    @Query("SELECT p.viewCount FROM ProfileEntity p WHERE p.id = :profileId")
    int getViewCount(@Param("profileId") Long profileId);

    @Modifying
    @Query("UPDATE ProfileEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :profileId")
    void incrementViewCount(@Param("profileId") Long profileId);
}
