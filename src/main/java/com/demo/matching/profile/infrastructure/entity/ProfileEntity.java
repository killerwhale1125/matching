package com.demo.matching.profile.infrastructure.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.member.infrastructure.entity.MemberEntity;
import com.demo.matching.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.demo.matching.core.common.service.SafeEntityMapper.mapIfInitialized;
import static com.demo.matching.core.common.service.SafeEntityMapper.mapIfNotNull;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "profile")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class ProfileEntity extends BaseTimeEntity {
    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private int viewCount;

    /* Domain -> Entity 변환 */
    public static ProfileEntity from(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.id = profile.getId();
        entity.viewCount = profile.getViewCount();
        entity.createdTime = profile.getCreatedTime();
        entity.modifiedTime = profile.getModifiedTime();
        /* Null 체크 */
        entity.member = mapIfNotNull(MemberEntity::from, profile.getMember());
        return entity;
    }

    /* Entity -> Domain 변환 */
    public Profile to() {
        Profile.ProfileBuilder builder = Profile.builder()
                .id(id)
                .viewCount(viewCount)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime);
        /* JPA Proxy 체크 */
        builder.member(mapIfInitialized(MemberEntity::to, member));
        return builder.build();
    }

}
