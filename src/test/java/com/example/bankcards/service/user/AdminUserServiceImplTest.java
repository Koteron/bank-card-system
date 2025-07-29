package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    @Test
    void deleteUserById_shouldCallRepositoryDeleteById() {
        UUID id = UUID.randomUUID();
        adminUserService.deleteUserById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void getUsers_shouldReturnMappedUserDtos() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = List.of(user1, user2);
        UserDto dto1 = UserDto.builder().build();
        UserDto dto2 = UserDto.builder().build();

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> result = adminUserService.getUsers();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
        verify(userRepository).findAll();
        verify(userMapper).toUserDto(user1);
        verify(userMapper).toUserDto(user2);
    }

    @Test
    void getUserById_shouldReturnMappedUserDto() {
        UUID id = UUID.randomUUID();
        User user = new User();
        UserDto dto = UserDto.builder().build();

        when(userService.getEntityById(id)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserDto result = adminUserService.getUserById(id);

        assertEquals(dto, result);
        verify(userService).getEntityById(id);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void makeAdmin_shouldSetUserRoleToAdminAndSave() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setRole(UserRole.USER);
        when(userService.getEntityById(id)).thenReturn(user);

        adminUserService.makeAdmin(id);

        assertEquals(UserRole.ADMIN, user.getRole());
        verify(userService).getEntityById(id);
        verify(userRepository).save(user);
    }
}
