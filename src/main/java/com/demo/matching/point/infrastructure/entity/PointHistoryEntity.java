package com.demo.matching.point.infrastructure.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.point.domain.PointHistory;
import com.demo.matching.point.domain.enums.PointHistoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "point_history")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PointHistoryEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PointHistoryType type;

    private Long memberId;
    private long amount;

    public static PointHistoryEntity from(PointHistory pointHistory) {
        PointHistoryEntity entity = new PointHistoryEntity();
        entity.id = pointHistory.getId();
        entity.type = pointHistory.getType();
        entity.memberId = pointHistory.getMemberId();
        entity.amount = pointHistory.getAmount();
        entity.createdTime = pointHistory.getCreatedTime();
        entity.modifiedTime = pointHistory.getModifiedTime();
        return entity;
    }

    public PointHistory to() {
        return  PointHistory.builder()
                .id(id)
                .type(type)
                .memberId(memberId)
                .amount(amount)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .build();
    }
}
