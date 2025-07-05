package com.demo.matching.member.infrastructure.repository;

import com.demo.matching.member.infrastructure.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}
