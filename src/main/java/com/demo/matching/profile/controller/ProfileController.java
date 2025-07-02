package com.demo.matching.profile.controller;

import com.demo.matching.profile.controller.port.in.ProfileService;
import com.demo.matching.profile.controller.request.ProfileSearchRequest;
import com.demo.matching.profile.controller.response.ProfileDetailResponse;
import com.demo.matching.profile.controller.response.ProfileListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileDetailResponse> getProfileDetail(@PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getProfileDetail(profileId));
    }

    @GetMapping
    public ResponseEntity<ProfileListResponse> getProfiles(ProfileSearchRequest request) {
        return ResponseEntity.ok(profileService.getProfiles(request));
    }
}
