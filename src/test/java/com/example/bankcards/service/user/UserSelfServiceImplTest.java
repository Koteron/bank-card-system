package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSelfServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthService authService;

    private UserSelfServiceImpl userSelfService;

    private User user;
    private OldNewPasswordDto oldNewPasswordDto;
    private AuthResponseDto authResponse;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userSelfService = new UserSelfServiceImpl(userRepository, passwordUtil, userMapper, authService);

        user = new User();
        user.setPasswordHash("oldHash");
        user.setEmail("old@example.com");
        user.setNickname("OldNick");

        oldNewPasswordDto = new OldNewPasswordDto("oldPass", "newPass");
        authResponse = new AuthResponseDto(
                UUID.fromString("7d6a2324-6151-4085-a147-56049c4a5cab"),
                "nickname",
                "test@example.com",
                UserRole.USER,
                "token");
        userDto = UserDto.builder().build();
    }

    @Test
    void changePassword_correctOldPassword_savesNewHash() {
        String initialHash = user.getPasswordHash();
        when(passwordUtil.matches(oldNewPasswordDto.oldPassword(), initialHash)).thenReturn(true);
        when(passwordUtil.encrypt(oldNewPasswordDto.newPassword())).thenReturn("newHash");

        userSelfService.changePassword(user, oldNewPasswordDto);

        assertEquals("newHash", user.getPasswordHash());
        verify(passwordUtil).matches(oldNewPasswordDto.oldPassword(), initialHash);
        verify(passwordUtil).encrypt(oldNewPasswordDto.newPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_wrongOldPassword_throwsForbidden() {
        String initialHash = user.getPasswordHash();
        when(passwordUtil.matches(oldNewPasswordDto.oldPassword(), initialHash)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> userSelfService.changePassword(user, oldNewPasswordDto));
        verify(passwordUtil).matches(oldNewPasswordDto.oldPassword(), initialHash);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changeEmail_uniqueEmail_returnsAuthResponse() {
        String newEmail = "new@example.com";
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        user.setEmail(newEmail);
        when(userRepository.save(user)).thenReturn(user);
        when(authService.generateToken(newEmail)).thenReturn("newToken");
        when(userMapper.toAuthResponseDto(user, "newToken")).thenReturn(authResponse);

        AuthResponseDto result = userSelfService.changeEmail(user, newEmail);

        assertEquals(authResponse, result);
        verify(userRepository).existsByEmail(newEmail);
        verify(userRepository).save(user);
        verify(authService).generateToken(newEmail);
        verify(userMapper).toAuthResponseDto(user, "newToken");
    }

    @Test
    void changeEmail_existingEmail_throwsForbidden() {
        String newEmail = "new@example.com";
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        assertThrows(ForbiddenException.class, () -> userSelfService.changeEmail(user, newEmail));
        verify(userRepository).existsByEmail(newEmail);
        verifyNoMoreInteractions(userRepository, authService, userMapper);
    }

    @Test
    void changeNickname_updatesNicknameAndReturnsDto() {
        String newNickname = "NewNick";
        user.setNickname(newNickname);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userSelfService.changeNickname(user, newNickname);

        assertEquals(userDto, result);
        assertEquals(newNickname, user.getNickname());
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void deleteUser_callsRepositoryDelete() {
        doNothing().when(userRepository).delete(user);

        userSelfService.deleteUser(user);

        verify(userRepository).delete(user);
    }
}
