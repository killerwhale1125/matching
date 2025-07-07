package com.demo.matching.member.application;

import com.demo.matching.member.application.dto.MemberInfoResponse;
import com.demo.matching.member.application.port.in.MemberRepository;
import com.demo.matching.member.application.usecase.PointPersistenceUseCase;
import com.demo.matching.member.application.usecase.ProfilePersistenceUseCase;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.PointInfo;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.member.presentation.port.in.MemberService;
import com.demo.matching.member.presentation.request.MemberSignup;
import com.demo.matching.member.presentation.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MemberServiceTest {
    private MemberService mockMemberService;
    private MemberRepository mockMemberRepository;
    private ProfilePersistenceUseCase mockProfilePersistenceUseCase;
    private PointPersistenceUseCase mockPointPersistenceUseCase;

    @Test
    @DisplayName("회원가입 시 회원 정보를 통해 Point, Profile 이 함께 생성된다")
    void signup() {
        // given
        final MemberSignup memberSignup = new MemberSignup("이름");

        // 회원 저장 결과로 생성된 Member 객체 가정
        final Member mockMember = Member.builder().id(1L).name("이름").build();

        // 프로필/포인트 생성 결과
        ProfileInfo profileInfo = new ProfileInfo(0);
        PointInfo pointInfo = new PointInfo(0);

        // mocking
        when(mockMemberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(mockProfilePersistenceUseCase.createBy(mockMember)).thenReturn(profileInfo);
        when(mockPointPersistenceUseCase.createBy(mockMember.getId())).thenReturn(pointInfo);

        // when
        final MemberResponse result = mockMemberService.signup(memberSignup);

        // then
        assertThat(result.name()).isEqualTo("이름");
        assertThat(result.profileInfo()).isEqualTo(profileInfo);
        assertThat(result.pointInfo()).isEqualTo(pointInfo);
    }

    @Test
    @DisplayName("회원 ID로 회원 정보를 조회한다.")
    void getById() {
        // given
        final Long memberId = 1L;
        Member member = Member.builder().id(1L).name("이름").build();
        when(mockMemberRepository.findById(memberId)).thenReturn(member);

        // when
        final MemberInfoResponse result = mockMemberService.getById(memberId);

        // then
        assertThat(result.memberId()).isEqualTo(memberId);
    }

    /**
     * DB 대용 Mock 어댑터 데이터 추가
     */
    @BeforeEach
    void setUp() {
        mockMemberRepository = mock(MemberRepository.class);
        mockProfilePersistenceUseCase = mock(ProfilePersistenceUseCase.class);
        mockPointPersistenceUseCase = mock(PointPersistenceUseCase.class);
        mockMemberService = new MemberServiceImpl(mockMemberRepository, mockProfilePersistenceUseCase, mockPointPersistenceUseCase);
    }

}
