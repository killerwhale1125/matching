package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.profile.infrastructure.entity.ProfileEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    /* 프로필 & Member fetch join */
    @EntityGraph(attributePaths = {"member"})
    Optional<ProfileEntity> findWithMemberById(Long id);

    @Query("SELECT p.viewCount FROM ProfileEntity p WHERE p.id = :profileId")
    Optional<Integer> getViewCount(@Param("profileId") Long profileId);
}
