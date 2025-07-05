package com.demo.matching.profile.presentation;

import com.demo.matching.member.domain.Member;
import com.demo.matching.member.domain.dto.ProfileInfo;
import com.demo.matching.profile.presentation.port.in.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileInternalReceiver {
    private final ProfileService profileService;

    public ProfileInfo createBy(Member member) {
        return profileService.create(member);
    }
}
