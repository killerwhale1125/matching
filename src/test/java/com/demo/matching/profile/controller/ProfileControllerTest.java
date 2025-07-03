package com.demo.matching.profile.controller;

import com.demo.matching.profile.config.ProfileMockConfig;
import com.demo.matching.profile.domain.ProfileSortType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import(ProfileMockConfig.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("프로필 상세 조회 요청 시 200 OK와 ProfileDetailResponse 반환")
    void getProfileDetail_OK() throws Exception {
        // given
        final Long profileId = 1L;

        // when & then
        mockMvc.perform(get("/api/profiles/{profileId}", profileId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.viewCount").value(1));
    }

    @Test
    @DisplayName("프로필 List 조회 요청 시 200 OK와 List<MemberProfileResponse> 반환")
    void getProfiles_OK() throws Exception {
        // given
        final ProfileSortType profileSortType = ProfileSortType.LATEST;
        final Integer page = 0;
        final Integer size = 5;

        // when & then
        mockMvc.perform(get("/api/profiles")
                        .param("profileSortType", profileSortType.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("테스트이름"))
                .andExpect(jsonPath("$.result[0].viewCount").value(0));
    }
}
