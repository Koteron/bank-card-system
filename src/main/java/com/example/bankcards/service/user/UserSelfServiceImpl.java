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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserSelfServiceImpl implements UserSelfService {
    private static final Logger log = LoggerFactory.getLogger(UserSelfServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final UserMapper userMapper;
    private final AuthService authService;

    @Override
    public void changePassword(User user, OldNewPasswordDto oldNewPasswordDto) {
        log.info("Changing password of user with id {}", user.getId());
        if (!passwordUtil.matches(oldNewPasswordDto.oldPassword(), user.getPasswordHash())) {
            throw new ForbiddenException("Given password does not match the current one");
        }
        user.setPasswordHash(passwordUtil.encrypt(oldNewPasswordDto.newPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto changeEmail(User user, String newEmail) {
        log.info("Changing email of user {} to {}", user.getEmail(), newEmail);
        if (userRepository.existsByEmail(newEmail)) {
            throw new ForbiddenException(String.format("Email %s is already in use", newEmail));
        }
        user.setEmail(newEmail);
        return userMapper.toAuthResponseDto(userRepository.save(user), authService.generateToken(newEmail));
    }

    @Override
    public UserDto changeNickname(User user, String newNickname) {
        log.info("Changing nickname of user {} to {}", user.getNickname(), newNickname);
        user.setNickname(newNickname);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(User user) {
        log.info("Deleting user {}", user.getNickname());
        userRepository.delete(user);
    }
}
