package com.demo.matching.profile.application;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.profile.presentation.port.in.ProfileService;
import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.presentation.response.ProfileDetailResponse;
import com.demo.matching.profile.presentation.response.MemberProfileResponse;
import com.demo.matching.profile.domain.Profile;
import com.demo.matching.profile.domain.enums.ProfileSortType;
import com.demo.matching.profile.infrastructure.querydsl.dto.MemberProfile;
import com.demo.matching.profile.application.port.in.ProfileQueryRepository;
import com.demo.matching.profile.application.port.in.ProfileRepository;
import com.demo.matching.profile.application.port.out.ProfileViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileQueryRepository profileQueryRepository;
    private final ProfileViewCountPort profileViewCountPort;

    /**
     * 프로필 상세 조회 API
     * Redis에서 조회 수 판별하고 업데이트 후 응답
     */
    @Override
    public ProfileDetailResponse getProfileDetail(Long profileId) {
        Profile profile = profileRepository.findById(profileId);
        return ProfileDetailResponse.from(profileViewCountPort.increaseViewCount(profile));
    }

    /**
     * 프로필 List 조회 API
     */
    @Override
    public List<MemberProfileResponse> getProfiles(ProfileSearchRequest request) {
        // 기본 정렬 : 등록일 내림차순 ( DB 에서 처리 - Index 설정 시 단일 Index 로 Index 테이블 관리 비용 및 성능 향상 )
        // 그 외 code level 정렬
        List<MemberProfile> memberProfiles = profileQueryRepository.getProfiles(request);
        if (memberProfiles == null || memberProfiles.isEmpty()) return List.of();

        /* 캐시된 조회수와 DB 조회수 동기화 */
        syncViewCountWithCache(memberProfiles, profileViewCountPort.getViewCountsBy(memberProfiles));

        /* 선택 정렬 조건 적용 (기본: 최신순 → 정렬 없음) */
        sortMemberProfiles(memberProfiles, request.profileSortType());

        return memberProfiles.stream().map(MemberProfileResponse::from).toList();
    }

    @Override
    public ProfileInfo create(Member member) {
        return ProfileInfo.from(profileRepository.save(Profile.create(member)).getViewCount());
    }

    /**
     * 캐시된 조회수가 있을 경우에만 조회수를 덮어씌움
     */
    private void syncViewCountWithCache(List<MemberProfile> memberProfiles, Map<Long, Integer> cachedMap) {
        /* 캐시 Map 에 key 값이 포함되어 있을 경우에만 캐싱된 조회수 값을 동기화 */
        for (MemberProfile p : memberProfiles) {
            if (cachedMap.containsKey(p.getProfileId())) {
                p.syncViewCount(cachedMap.get(p.getProfileId()));
            }
        }
    }

    /**
     * 선택적 정렬 조건 처리
     * - NAME: 이름 가나다순
     * - VIEWS: 조회수 내림차순
     * 기본 정렬(등록일 내림차순)은 DB 에서 처리됨
     */
    private void sortMemberProfiles(List<MemberProfile> memberProfiles, ProfileSortType sortType) {
        /* Stream 으로 새로운 객체 생성 시 메모리 낭비로 Collection Sort 사용 */
        switch (sortType) {
            // 1. 가나다 순
            case NAME -> memberProfiles.sort(Comparator.comparing(MemberProfile::getMemberName));
            case VIEWS -> memberProfiles.sort(Comparator.comparingInt(MemberProfile::getViewCount).reversed());
        }
    }
}
