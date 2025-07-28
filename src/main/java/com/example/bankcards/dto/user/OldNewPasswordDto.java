package com.example.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;

public record OldNewPasswordDto(
        @NotBlank
        String newPassword,

        @NotBlank
        String oldPassword
) {
}
