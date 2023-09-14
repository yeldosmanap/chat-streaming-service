package com.kcell.testtask.messaging.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcell.testtask.messaging.controller.base.BaseControllerTest;
import com.kcell.testtask.messaging.dto.request.LoginRequestDto;
import com.kcell.testtask.messaging.dto.request.RegisterRequestDto;
import com.kcell.testtask.messaging.dto.response.LoginResponseDto;
import com.kcell.testtask.messaging.dto.response.RegisterResponseDto;
import com.kcell.testtask.messaging.model.Role;
import com.kcell.testtask.messaging.repository.MessageRepository;
import com.kcell.testtask.messaging.repository.TokenRepository;
import com.kcell.testtask.messaging.repository.UserRepository;
import com.kcell.testtask.messaging.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kcell.testtask.messaging.controller.config.ConfigurationForControllersTest.MOCK_USERNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController is working when ...")
public class AuthControllerTest extends BaseControllerTest {
    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    TokenRepository tokenRepository;

    @MockBean
    MessageRepository messageRepository;

    @MockBean
    UserRepository userRepository;

    @WithUserDetails(value = MOCK_USERNAME)
    @Test
    public void register_user() throws Exception {
        RegisterRequestDto request =
                new RegisterRequestDto(
                        "testuser",
                        "test@example.com",
                        "password123",
                        Role.USER);

        RegisterResponseDto response = RegisterResponseDto.builder()
                .success(true)
                .message("User registered successfully")
                .build();

        when(authenticationService.register(any(RegisterRequestDto.class))).thenReturn(response);

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/registration")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString(); // Get the JSON response as a String

        ObjectMapper objectMapper = new ObjectMapper();
        var responseDto = objectMapper.readValue(jsonResponse, RegisterResponseDto.class);

        assertTrue(responseDto.isSuccess());
        assertNull(responseDto.getError());
    }

    @Test
    public void login_user() throws Exception {
        LoginRequestDto request = new LoginRequestDto(
                "test@example.com",
                "password123");

        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken("sampleAccessToken")
                .succeed(true)
                .build();

        when(authenticationService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.succeed").value(true))
                .andReturn();
    }

    // Utility method to convert objects to JSON
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
