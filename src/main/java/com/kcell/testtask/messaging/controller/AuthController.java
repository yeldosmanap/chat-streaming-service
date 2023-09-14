package com.kcell.testtask.messaging.controller;

import com.kcell.testtask.messaging.dto.request.LoginRequestDto;
import com.kcell.testtask.messaging.dto.request.RegisterRequestDto;
import com.kcell.testtask.messaging.dto.response.LoginResponseDto;
import com.kcell.testtask.messaging.dto.response.RegisterResponseDto;
import com.kcell.testtask.messaging.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registration")
    public RegisterResponseDto registerUser(@RequestBody RegisterRequestDto request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public LoginResponseDto loginUser(@RequestBody LoginRequestDto userCreateDto) {
        return authenticationService.login(userCreateDto);
    }
}
