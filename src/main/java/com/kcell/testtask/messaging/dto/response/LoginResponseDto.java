package com.kcell.testtask.messaging.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    public String accessToken;
    public String message;
    public boolean succeed;
}
