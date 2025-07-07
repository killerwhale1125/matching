package com.demo.matching.profile.infrastructure.repository;

import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.application.dto.MemberProfile;
import com.demo.matching.profile.application.port.in.ProfileQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.demo.matching.member.infrastructure.entity.QMemberEntity.memberEntity;
import static com.demo.matching.profile.infrastructure.entity.QProfileEntity.profileEntity;

@Repository
@RequiredArgsConstructor
public class ProfileQueryRepositoryImpl implements ProfileQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 회원 프로필 List 조회 쿼리
     */
    @Override
    public List<MemberProfile> getProfiles(ProfileSearchRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        return queryFactory
            .select(Projections.constructor(
                MemberProfile.class,
                profileEntity.id,
                memberEntity.name,
                profileEntity.viewCount,
                profileEntity.createdTime
            ))
            .from(profileEntity)
            .join(profileEntity.member, memberEntity)
            .orderBy(profileEntity.createdTime.desc()) // 최신순 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
