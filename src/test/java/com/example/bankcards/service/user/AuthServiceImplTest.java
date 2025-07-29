package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserLoginDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRegisterDto registerDto;
    private UserLoginDto loginDto;
    private User user;
    private AuthResponseDto authResponse;
    private String token;
    private String passwordHash;

    @BeforeEach
    void setUp() {
        registerDto = new UserRegisterDto("nickname", "test@example.com", "password", UserRole.USER);
        loginDto = new UserLoginDto("test@example.com", "password");
        user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        token = "token";
        passwordHash = "hashed";
        authResponse = new AuthResponseDto(
                UUID.fromString("7d6a2324-6151-4085-a147-56049c4a5cab"),
                "nickname",
                "test@example.com",
                UserRole.USER,
                token);
    }

    @Test
    void register_successfulRegistration_returnsAuthResponse() {
        when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.empty());
        when(passwordUtil.encrypt(registerDto.password())).thenReturn(passwordHash);
        when(userMapper.toNewUserEntity(registerDto, passwordHash)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn(token);
        when(userMapper.toAuthResponseDto(user, token)).thenReturn(authResponse);

        AuthResponseDto result = authService.register(registerDto);

        assertEquals(authResponse, result);
        verify(userRepository).findByEmail(registerDto.email());
        verify(passwordUtil).encrypt(registerDto.password());
        verify(userMapper).toNewUserEntity(registerDto, passwordHash);
        verify(userRepository).save(user);
        verify(jwtTokenProvider).generateToken(user.getEmail());
        verify(userMapper).toAuthResponseDto(user, token);
    }

    @Test
    void register_existingEmail_throwsForbiddenException() {
        when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> authService.register(registerDto));
        verify(userRepository).findByEmail(registerDto.email());
        verifyNoMoreInteractions(passwordUtil, userMapper, jwtTokenProvider);
    }

    @Test
    void login_successfulLogin_returnsAuthResponse() {
        when(userRepository.findByEmail(loginDto.email())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(loginDto.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn(token);
        when(userMapper.toAuthResponseDto(user, token)).thenReturn(authResponse);

        AuthResponseDto result = authService.login(loginDto);

        assertEquals(authResponse, result);
        verify(userRepository).findByEmail(loginDto.email());
        verify(passwordUtil).matches(loginDto.password(), user.getPasswordHash());
        verify(jwtTokenProvider).generateToken(user.getEmail());
        verify(userMapper).toAuthResponseDto(user, token);
    }

    @Test
    void login_userNotFound_throwsNotFoundException() {
        when(userRepository.findByEmail(loginDto.email())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(loginDto));
        verify(userRepository).findByEmail(loginDto.email());
        verifyNoMoreInteractions(passwordUtil, jwtTokenProvider, userMapper);
    }

    @Test
    void login_wrongPassword_throwsUnauthorizedException() {
        when(userRepository.findByEmail(loginDto.email())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(loginDto.password(), user.getPasswordHash())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginDto));
        verify(userRepository).findByEmail(loginDto.email());
        verify(passwordUtil).matches(loginDto.password(), user.getPasswordHash());
        verifyNoMoreInteractions(jwtTokenProvider, userMapper);
    }

    @Test
    void generateToken_returnsJwtToken() {
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn(token);

        String result = authService.generateToken(user.getEmail());

        assertEquals(token, result);
        verify(jwtTokenProvider).generateToken(user.getEmail());
    }
}
