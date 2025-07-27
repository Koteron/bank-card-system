package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toNewUserEntity(UserRegisterDto userRegisterDto, String passwordHash) {
        return User.builder()
                .email(userRegisterDto.email())
                .nickname(userRegisterDto.nickname())
                .passwordHash(passwordHash)
                .role(userRegisterDto.role())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }

    public AuthResponseDto toAuthResponseDto(User user, String token) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .build();
    }

}
