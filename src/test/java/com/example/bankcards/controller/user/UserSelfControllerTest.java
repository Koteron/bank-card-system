package com.example.bankcards.controller.user;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.ChangeEmailDto;
import com.example.bankcards.dto.user.ChangeNicknameDto;
import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.user.UserSelfService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserSelfController.class)
@Import(TestSecurityConfig.class)
class UserSelfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserSelfService userSelfService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("user@example.com");
        testUser.setNickname("usernick");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void changePassword_withValidDto_returnsNoContent() throws Exception {
        OldNewPasswordDto dto = new OldNewPasswordDto("oldPass", "newPass");
        Mockito.doNothing().when(userSelfService).changePassword(eq(testUser), any(OldNewPasswordDto.class));

        mockMvc.perform(patch("/api/user/update/password")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

        Mockito.verify(userSelfService).changePassword(eq(testUser), eq(dto));
    }

    @Test
    void changeEmail_withValidDto_returnsAuthResponse() throws Exception {
        ChangeEmailDto dto = new ChangeEmailDto("new@example.com");
        UUID newId = UUID.randomUUID();
        AuthResponseDto response = new AuthResponseDto(
                newId,
                "newnick",
                "new@example.com",
                UserRole.USER,
                "newToken"
        );
        Mockito.when(userSelfService.changeEmail(eq(testUser), eq("new@example.com"))).thenReturn(response);

        mockMvc.perform(patch("/api/user/update/email")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(newId.toString()))
                .andExpect(jsonPath("$.nickname").value("newnick"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("newToken"));

        Mockito.verify(userSelfService).changeEmail(eq(testUser), eq("new@example.com"));
    }

    @Test
    void changeNickname_withValidDto_returnsUserDto() throws Exception {
        ChangeNicknameDto dto = new ChangeNicknameDto("newnick");
        UserDto response = UserDto.builder()
                .id(testUser.getId())
                .nickname("newnick")
                .email(testUser.getEmail())
                .role(testUser.getRole())
                .build();
        Mockito.when(userSelfService.changeNickname(eq(testUser), eq("newnick"))).thenReturn(response);

        mockMvc.perform(patch("/api/user/update/nickname")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.nickname").value("newnick"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        Mockito.verify(userSelfService).changeNickname(eq(testUser), eq("newnick"));
    }

    @Test
    void deleteUser_withAuthentication_returnsNoContent() throws Exception {
        Mockito.doNothing().when(userSelfService).deleteUser(eq(testUser));

        mockMvc.perform(delete("/api/user")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                )
                .andExpect(status().isNoContent());

        Mockito.verify(userSelfService).deleteUser(eq(testUser));
    }
}
