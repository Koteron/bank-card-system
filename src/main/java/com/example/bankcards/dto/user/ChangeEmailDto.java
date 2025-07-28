package com.example.bankcards.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDto(
        @Email @NotBlank(message = "Email cannot be blank")
        String newEmail
) {}
