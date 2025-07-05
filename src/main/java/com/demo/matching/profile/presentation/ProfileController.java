package com.demo.matching.profile.presentation;

import com.demo.matching.core.common.exception.BusinessResponse;
import com.demo.matching.profile.presentation.port.in.ProfileService;
import com.demo.matching.profile.presentation.request.ProfileSearchRequest;
import com.demo.matching.profile.presentation.response.MemberProfileResponse;
import com.demo.matching.profile.presentation.response.ProfileDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 회원 프로필 상세 조회 API
     */
    @GetMapping("/{profileId}")
    public BusinessResponse<ProfileDetailResponse> getProfileDetail(@PathVariable Long profileId) {
        return new BusinessResponse(profileService.getProfileDetail(profileId));
    }

    /**
     * 회원 프로필 List 조회 API
     */
    @GetMapping
    public BusinessResponse<List<MemberProfileResponse>> getProfiles(
            @Valid @ModelAttribute ProfileSearchRequest profileSearchRequest) {
        return new BusinessResponse(profileService.getProfiles(profileSearchRequest));
    }
}
