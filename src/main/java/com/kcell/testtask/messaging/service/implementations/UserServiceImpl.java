package com.kcell.testtask.messaging.service.implementations;

import com.kcell.testtask.messaging.dto.request.UserCreateRequestDto;
import com.kcell.testtask.messaging.dto.response.UserResponseDto;
import com.kcell.testtask.messaging.exception.UserNotFoundException;
import com.kcell.testtask.messaging.model.User;
import com.kcell.testtask.messaging.repository.UserRepository;
import com.kcell.testtask.messaging.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto saveUser(UserCreateRequestDto request) {
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new RuntimeException("User with username or email already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getUsername())
                .password(request.getUsername())
                .build();

        var response = userRepository.save(user);

        return UserResponseDto.builder()
                .id(response.getId())
                .username(response.getUsername())
                .email(response.getEmail())
                .build();
    }

    @Override
    public UserResponseDto deleteUserById(Long id) {
        var user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
