package com.kcell.testtask.messaging.controller;

import com.kcell.testtask.messaging.dto.request.UserCreateRequestDto;
import com.kcell.testtask.messaging.dto.response.UserResponseDto;
import com.kcell.testtask.messaging.service.UserService;
import com.kcell.testtask.messaging.service.implementations.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserCreateRequestDto userCreateDto) {
        return userService.saveUser(userCreateDto);
    }

    @DeleteMapping
    public String deleteUser(Long id) {
        userService.deleteUserById(id);

        return "User deleted";
    }
}
