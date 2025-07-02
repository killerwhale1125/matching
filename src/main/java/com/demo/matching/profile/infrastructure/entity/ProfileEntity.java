package com.demo.matching.profile.infrastructure.entity;

import com.demo.matching.common.jpa.BaseTimeEntity;
import com.demo.matching.member.infrastructure.entity.MemberEntity;
import com.demo.matching.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

import static com.demo.matching.common.util.SafeEntityMapper.*;
import static com.demo.matching.profile.domain.Profile.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

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

    public static ProfileEntity from(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.id = profile.getId();
        entity.viewCount = profile.getViewCount();
        entity.member = mapIfNotNull(MemberEntity::from, profile.getMember());
        return entity;
    }

    public Profile to() {
        ProfileBuilder builder = builder()
                .id(id)
                .viewCount(viewCount)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime);
        builder.member(mapIfInitialized(MemberEntity::to, member));
        return builder.build();
    }

}
