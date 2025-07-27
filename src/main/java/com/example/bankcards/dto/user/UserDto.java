package com.example.bankcards.dto.user;

import com.example.bankcards.entity.util.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto (
    UUID id,

    String nickname,

    String email,

    UserRole role
) {

}
