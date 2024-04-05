package ru.maeasoftoworks.normativecontrol.api.rest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Slf4j
//TODO: Fix tests
public class BasicRestTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void shouldReturnDefaultMessage() throws Exception {
        when(authService.loginByCredentials("P.O.Kurchatov@urfu.me", "admin_password")).thenReturn(new Jwt[]{null, null});

        this.mockMvc.perform(post("/auth/login").content("{\"email\": \"P.O.Kurchatov@urfu.me\", \"password\": \"admin_password\"}").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("accessToken")))
                .andExpect(content().string(containsString("refreshToken")));
    }
}
