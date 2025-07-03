package com.demo.matching.member.controller;

import com.demo.matching.member.config.MemberMockConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(MemberMockConfig.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 요청 시 200 OK와 MemberResponse 반환")
    void signup_OK() throws Exception {
        // when + then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"테스트이름\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("테스트이름"))
                .andExpect(jsonPath("$.result.profileDetail").exists());
    }
}
