package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserLoginDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordUtil passwordUtil;
    private final UserMapper userMapper;

    public AuthResponseDto register(UserRegisterDto userRegisterDto) {
        Optional<User> foundUser = userRepository.findByEmail(userRegisterDto.email());
        if (foundUser.isPresent()) {
            log.error("User {} already exists", userRegisterDto.email());
            throw new ForbiddenException("Email is already in use!");
        }
        String passwordHash = passwordUtil.encrypt(userRegisterDto.password());
        User newUser = userRepository.save(userMapper.toNewUserEntity(userRegisterDto, passwordHash));
        return userMapper.toAuthResponseDto(newUser, jwtTokenProvider.generateToken(newUser.getEmail()));
    }

    public AuthResponseDto login(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.email())
                .orElseThrow(() -> new NotFoundException("User not found!"));
        if (passwordUtil.matches(userLoginDto.password(), user.getPasswordHash())) {
            return userMapper.toAuthResponseDto(user, generateToken(user.getEmail()));
        }
        throw new UnauthorizedException("Wrong password!");
    }

    public String generateToken(String email) {
        return jwtTokenProvider.generateToken(email);
    }
}
