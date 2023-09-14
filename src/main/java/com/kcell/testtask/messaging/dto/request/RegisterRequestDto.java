package com.kcell.testtask.messaging.dto.request;

import com.kcell.testtask.messaging.model.Role;
import com.kcell.testtask.messaging.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
    private Role role;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
