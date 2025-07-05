package com.demo.matching.member.application.usecase;

import com.demo.matching.member.application.port.out.ProfileProvider;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilePersistenceUseCase {
    private final ProfileProvider profileProvider;

    public ProfileInfo createBy(Member member) {
        return profileProvider.createBy(member);
    }
}
