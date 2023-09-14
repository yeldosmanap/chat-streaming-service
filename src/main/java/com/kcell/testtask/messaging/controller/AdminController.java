package com.kcell.testtask.messaging.controller;

import com.kcell.testtask.messaging.dto.response.UserResponseDto;
import com.kcell.testtask.messaging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;

    @DeleteMapping
    public UserResponseDto deleteUser(Long id) {
        return userService.deleteUserById(id);
    }
}
