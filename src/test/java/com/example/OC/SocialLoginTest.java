package com.example.OC;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfigureMockMvc
public class SocialLoginTest {

    @Autowired
    private MockMvc mockMvc ;

    @Test
    public void testOAuth2Login() throws Exception {
        mockMvc.perform(get("/endpoint")
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("expectedValue")));
    }
}
