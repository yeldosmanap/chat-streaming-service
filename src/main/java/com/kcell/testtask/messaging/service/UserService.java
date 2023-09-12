package com.kcell.testtask.messaging.service;

import com.kcell.testtask.messaging.dto.request.UserCreateRequestDto;
import com.kcell.testtask.messaging.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto saveUser(UserCreateRequestDto request);
    UserResponseDto deleteUserById(Long id);
}
