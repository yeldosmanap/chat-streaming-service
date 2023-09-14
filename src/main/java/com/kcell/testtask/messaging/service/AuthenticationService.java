package com.kcell.testtask.messaging.service;

import com.kcell.testtask.messaging.dto.request.LoginRequestDto;
import com.kcell.testtask.messaging.dto.request.RegisterRequestDto;
import com.kcell.testtask.messaging.dto.response.LoginResponseDto;
import com.kcell.testtask.messaging.dto.response.RegisterResponseDto;

public interface AuthenticationService {
    RegisterResponseDto register(RegisterRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
}
