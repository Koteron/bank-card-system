package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    void deleteUserById(UUID id);
    List<UserDto> getUsers();
    UserDto getUserById(UUID id);
    void makeAdmin(UUID id);
}
