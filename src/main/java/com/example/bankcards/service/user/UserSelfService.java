package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;

public interface UserSelfService {
    void changePassword(User user, OldNewPasswordDto oldNewPasswordDto);
    UserDto changeEmail(User user, String email);
    UserDto changeNickname(User user, String firstName);
    void deleteUser(User user);
}
