package com.example.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;

public record ChangeNicknameDto(
        @NotBlank(message = "Email cannot be blank")
        String newNickname
) {}
