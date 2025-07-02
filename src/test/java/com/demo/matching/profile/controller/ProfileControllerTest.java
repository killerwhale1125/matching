package com.demo.matching.profile.controller;

import com.demo.matching.profile.config.ProfileMockConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProfileController.class)
@Import(ProfileMockConfig.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("프로필 상세 조회 요청 시 200 OK와 ProfileDetailResponse 반환")
    void getProfileDetail_OK() throws Exception {
        // given
        Long profileId = 1L;

        // when & then
        mockMvc.perform(get("/api/profile/{profileId}", profileId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewCount").value(1));
    }
}
