package com.example.bankcards.dto.user;

import com.example.bankcards.entity.util.UserRole;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDto(
        @NotBlank(message = "Nickname cannot be blank")
        String nickname,

        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password,

        UserRole role
)
{ }
