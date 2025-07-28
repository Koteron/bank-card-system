package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nickname", source = "dto.nickname")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "role", source = "dto.role")
    User toNewUserEntity(UserRegisterDto dto, String passwordHash);

    UserDto toUserDto(User user);

    @Mapping(target = "token", source = "token")
    AuthResponseDto toAuthResponseDto(User user, String token);
}
