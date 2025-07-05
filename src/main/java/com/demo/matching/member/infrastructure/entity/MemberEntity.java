package com.demo.matching.member.infrastructure.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberEntity extends BaseTimeEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /* Domain -> Entity 변환 */
    public static MemberEntity from(Member member) {
        MemberEntity entity = new MemberEntity();
        entity.id = member.getId();
        entity.name = member.getName();
        entity.createdTime = member.getCreatedTime();
        entity.modifiedTime = member.getModifiedTime();
        return entity;
    }

    /* Entity -> Domain 변환 */
    public Member to() {
        return Member.builder()
                .id(id)
                .name(name)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .build();
    }
}
