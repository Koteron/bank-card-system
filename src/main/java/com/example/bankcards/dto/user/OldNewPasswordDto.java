package com.example.bankcards.dto.user;

import lombok.NonNull;

public record OldNewPasswordDto(
        @NonNull
        String newPassword,

        @NonNull
        String oldPassword
) {
}
