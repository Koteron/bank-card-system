package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserServiceImpl implements AdminUserService {
    private static final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public void deleteUserById(UUID id) {
        log.info("Deleting user by id: {} to admin", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Returning user list to admin");
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUserById(UUID id) {
        log.info("Getting user with id {} to admin", id);
        return userMapper.toUserDto(userService.getEntityById(id));
    }

    @Override
    public void makeAdmin(UUID id) {
        log.info("Making user with id {} an admin", id);
        User user = userService.getEntityById(id);
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
    }
}
