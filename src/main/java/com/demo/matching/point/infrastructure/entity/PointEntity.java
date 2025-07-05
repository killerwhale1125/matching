package com.demo.matching.point.infrastructure.entity;

import com.demo.matching.core.common.infrastructure.BaseTimeEntity;
import com.demo.matching.point.domain.Point;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "point")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PointEntity extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private long point;

    public static PointEntity from(Point point) {
        PointEntity entity = new PointEntity();
        entity.id = point.getId();
        entity.memberId = point.getMemberId();
        entity.point = point.getPoint();
        entity.createdTime = point.getCreatedTime();
        entity.modifiedTime = point.getModifiedTime();
        return entity;
    }

    public Point to() {
        return Point.builder()
                .id(id)
                .memberId(memberId)
                .point(point)
                .createdTime(createdTime)
                .modifiedTime(modifiedTime)
                .build();
    }
}
