package com.kcell.testtask.messaging.service.implementations;

import com.kcell.testtask.messaging.dto.request.LoginRequestDto;
import com.kcell.testtask.messaging.dto.request.RegisterRequestDto;
import com.kcell.testtask.messaging.dto.response.LoginResponseDto;
import com.kcell.testtask.messaging.dto.response.RegisterResponseDto;
import com.kcell.testtask.messaging.exception.UserNotFoundException;
import com.kcell.testtask.messaging.model.Token;
import com.kcell.testtask.messaging.model.TokenType;
import com.kcell.testtask.messaging.model.User;
import com.kcell.testtask.messaging.repository.TokenRepository;
import com.kcell.testtask.messaging.repository.UserRepository;
import com.kcell.testtask.messaging.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {
        // check user by email or username
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            return RegisterResponseDto.builder()
                    .success(false)
                    .error("User with this email or username already exists")
                    .build();
        }

        var user = request.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return RegisterResponseDto.builder()
                .success(true)
                .message("User registered successfully")
                .build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return LoginResponseDto.builder()
                .accessToken(jwtToken)
                .succeed(true).build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user);
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
