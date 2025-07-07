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
    @Query("UPDATE ProfileEntity p " +
            "SET p.viewCount = :totalViewCount " +
            "WHERE p.id = :profileId")
    int syncUpdateViewCountBy(@Param("profileId") Long profileId, @Param("totalViewCount") int totalViewCount);

    @Query("SELECT p.viewCount FROM ProfileEntity p WHERE p.id = :profileId")
    Optional<Integer> getViewCount(@Param("profileId") Long profileId);

//    @Modifying
//    @Query("UPDATE ProfileEntity p SET p.viewCountLoss = p.viewCountLoss + :loss WHERE p.id = :profileId")
//    void markViewCountLoss(@Param("profileId") Long profileId, @Param("loss") int loss);
}
