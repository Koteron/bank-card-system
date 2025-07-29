package com.example.bankcards.controller.user;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.user.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AdminUserController.class)
@Import(TestSecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("user@example.com");
        testUser.setNickname("usernick");
        testUser.setRole(UserRole.ADMIN);
    }

    @Test
    void deleteUserById_returnsNoContent_andCallsService() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/users/{user-id}", userId)
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminUserService).deleteUserById(userId);
    }

    @Test
    void getUserById_returnsUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto dto = UserDto.builder()
                .id(userId)
                .nickname("johndoe")
                .email("john.doe@example.com")
                .role(UserRole.USER)
                .build();

        Mockito.when(adminUserService.getUserById(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/admin/users/{user-id}", userId)
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.nickname").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void makeAdmin_returnsNoContent_andCallsService() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(patch("/api/admin/users/{user-id}", userId)
                    .with(user(new UserDetailsImpl(testUser)))
                    .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminUserService).makeAdmin(userId);
    }

    @Test
    void getUsers_returnsListOfUserDto() throws Exception {
        UserDto dto1 = UserDto.builder()
                .id(UUID.randomUUID())
                .nickname("alice")
                .email("alice@example.com")
                .role(UserRole.USER)
                .build();
        UserDto dto2 = UserDto.builder()
                .id(UUID.randomUUID())
                .nickname("bob")
                .email("bob@example.com")
                .role(UserRole.ADMIN)
                .build();
        List<UserDto> list = List.of(dto1, dto2);

        Mockito.when(adminUserService.getUsers()).thenReturn(list);

        mockMvc.perform(get("/api/admin/users")
                    .with(user(new UserDetailsImpl(testUser)))
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
