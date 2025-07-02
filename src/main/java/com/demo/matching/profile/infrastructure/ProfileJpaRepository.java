package com.demo.matching.profile.infrastructure;

import com.demo.matching.profile.infrastructure.entity.ProfileEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<ProfileEntity> findWithMemberById(Long id);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE profile SET view_count = LAST_INSERT_ID(view_count + 1) WHERE profile_id = :profileId")
    void incrementViewCount(@Param("profileId") Long profileId);

    /**
     * 위 incrementViewCount 쿼리와 반드시 같은 Transaction 내에서 사용해야 합니다.
     * DB 커넥션 분리 시 0값 반환함으로 주의해야합니다.
     */
    @Query(nativeQuery = true, value = "SELECT LAST_INSERT_ID()")
    long getLastUpdatedViewCount();

    @Modifying
    @Query("UPDATE ProfileEntity p SET p.viewCount = p.viewCount + :viewCount WHERE p.id = :profileId")
    void incrementViewCountBy(@Param("profileId") Long profileId, @Param("viewCount") int viewCount);

    @Query("SELECT p.viewCount FROM ProfileEntity p WHERE p.id = :profileId")
    int getViewCount(@Param("profileId") Long profileId);


    void incrementViewCountByBulk(Map<Long, Integer> bulkUpdateMap);
}
