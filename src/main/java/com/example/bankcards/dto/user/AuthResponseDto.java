package com.example.bankcards.dto.user;

import com.example.bankcards.entity.util.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResponseDto(
        UUID id,

        String nickname,

        String email,

        UserRole role,

        String token
)
{ }
