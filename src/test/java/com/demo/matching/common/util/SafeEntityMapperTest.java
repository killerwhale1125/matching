package com.demo.matching.common.util;

import com.demo.matching.core.common.service.SafeEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SafeEntityMapperTest {

    static class Dummy {
        private final String value;
        Dummy(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    @Test
    @DisplayName("mapIfNotNull - null이 아닌 경우 mapper 적용")
    void mapIfNotNull_apply() {
        final Dummy dummy = new Dummy("hello");

        final String result = SafeEntityMapper.mapIfNotNull(Dummy::getValue, dummy);

        assertThat(result).isEqualTo("hello");
    }

    @Test
    @DisplayName("mapIfNotNull - null인 경우 null 반환")
    void mapIfNotNull_null() {
        final Dummy dummy = null;

        final String result = SafeEntityMapper.mapIfNotNull(Dummy::getValue, dummy);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("mapIfInitialized - 초기화된 경우 mapper 적용")
    void mapIfInitialized_loaded() {
        final Dummy dummy = new Dummy("world");

        // 테스트 환경에서는 항상 로딩된 객체로 간주됨
        final String result = SafeEntityMapper.mapIfInitialized(Dummy::getValue, dummy);

        assertThat(result).isEqualTo("world");
    }

    @Test
    @DisplayName("mapIfInitialized - null인 경우 null 반환")
    void mapIfInitialized_null() {
        final Dummy dummy = null;

        final String result = SafeEntityMapper.mapIfInitialized(Dummy::getValue, dummy);

        assertThat(result).isNull();
    }

}
