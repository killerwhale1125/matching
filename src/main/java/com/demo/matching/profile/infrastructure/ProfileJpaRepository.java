package com.demo.matching.profile.infrastructure;

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

    /**
     * RETURNING 기능 MySQL 8.0.20 이상 버전에서만 사용 가능
     * MySQL 엔진 수준의 Atomic 연산으로 동시성에 안전 ( InnoDB의 Row-Level Lock 지원 )
     * MyISAM 스토리지 엔진 사용 시 동시성 문제로 해당 쿼리 절대 사용 금지 X ( MyISAM Row-Level Lock 지원 안함 )
     */
    @Modifying
    @Query(value = "UPDATE profile SET view_count = view_count + 1 WHERE profile_id = :profileId RETURNING view_count", nativeQuery = true)
    int incrementViewCountAndReturn(@Param("profileId") Long profileId);

    /**
     * 조회수 DB + 1 증가 후 세션에 증가 값 저장
     * MySQL 8.0.20 미만 버전일 경우 사용 가능
     * 반드시 아래 getLastUpdatedViewCount 와 같은 Transaction 내에서 사용해야 합니다.
     */
    @Deprecated
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE profile SET view_count = LAST_INSERT_ID(view_count + 1) WHERE profile_id = :profileId")
    void incrementViewCount(@Param("profileId") Long profileId);

    /**
     * 세션에 저장된 마지막으로 증가한 조회수 값 조회
     * MySQL 8.0.20 버전 이상일 경우 사용 X
     * 반드시 위 incrementViewCount 쿼리와 같은 Transaction 내에서 사용해야 합니다.
     * DB 커넥션 분리 시 0값 반환함으로 주의해야합니다.
     */
    @Deprecated
    @Query(nativeQuery = true, value = "SELECT LAST_INSERT_ID()")
    long getLastUpdatedViewCount();
}
