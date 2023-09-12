package com.kcell.testtask.messaging.dto.request;

import lombok.Data;

@Data
public class UserCreateRequestDto {
    private String username;
    private String email;
    private String password;
}
