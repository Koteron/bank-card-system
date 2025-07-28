package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserSelfServiceImpl implements UserSelfService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final UserMapper userMapper;
    private final AuthService authService;

    @Override
    public void changePassword(User user, OldNewPasswordDto oldNewPasswordDto) {
        if (!passwordUtil.matches(oldNewPasswordDto.oldPassword(), user.getPasswordHash())) {
            throw new ForbiddenException("Given password does not match the current one");
        }
        user.setPasswordHash(passwordUtil.encrypt(oldNewPasswordDto.newPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto changeEmail(User user, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new ForbiddenException("Email is already in use");
        }
        user.setEmail(newEmail);
        return userMapper.toAuthResponseDto(userRepository.save(user), authService.generateToken(newEmail));
    }

    @Override
    public UserDto changeNickname(User user, String newNickname) {
        user.setNickname(newNickname);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
