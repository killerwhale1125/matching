package com.demo.matching.common.util;

import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SafeEntityMapper {

    /**
     * Null 체크 후 매핑
     */
    public static <T, R> R mapIfNotNull(Function<T, R> mapper, T entity) {
        return entity == null ? null : mapper.apply(entity);
    }

    /**
     *  LAZY 로딩으로 인한 Proxy 체크
     *  entity null O -> null 반환k
     *  entity null X -> 명시적으로 가져온 실제 Entity -> Domain 변환 매핑
     */
    public static <T, R> R mapIfInitialized(Function<T, R> mapper, T entity) {
        return (entity == null || !Persistence.getPersistenceUtil().isLoaded(entity))
                ? null : mapper.apply(entity);
    }
}
