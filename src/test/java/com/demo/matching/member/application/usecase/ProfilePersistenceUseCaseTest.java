package com.demo.matching.member.application.usecase;

import com.demo.matching.member.application.port.out.ProfileProvider;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfilePersistenceUseCaseTest {

    private ProfilePersistenceUseCase mockProfileUseCase;
    private ProfileProvider mockProfileProvider;
    @Test
    @DisplayName("외부 port로 프로필 생성 후 DTO 조회")
    void createBy() {
        // given
        final Member member = Member.builder().id(1L).name("이름").build();
        ProfileInfo fakeProfile = new ProfileInfo(0);
        Mockito.when(mockProfileProvider.createBy(member)).thenReturn(fakeProfile);

        // when
        ProfileInfo result = mockProfileUseCase.createBy(member);

        // then
        assertThat(result).isNotNull();
        assertThat(result.viewCount()).isEqualTo(0);
    }

    @BeforeEach
    void setUp() {
        mockProfileProvider = Mockito.mock(ProfileProvider.class);
        mockProfileUseCase = new ProfilePersistenceUseCase(mockProfileProvider);
    }
}
