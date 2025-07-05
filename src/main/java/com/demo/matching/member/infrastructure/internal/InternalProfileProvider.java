package com.demo.matching.member.infrastructure.internal;

import com.demo.matching.member.application.port.out.ProfileProvider;
import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.profile.presentation.ProfileInternalReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternalProfileProvider implements ProfileProvider {
    private final ProfileInternalReceiver profileInternalReceiver;

    @Override
    public ProfileInfo createBy(Member member) {
        return profileInternalReceiver.createBy(member);
    }
}
