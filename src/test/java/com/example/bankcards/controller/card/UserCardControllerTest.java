package com.example.bankcards.controller.card;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.card.UserCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserCardController.class)
@Import(TestSecurityConfig.class)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCardService userCardService;

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
    void searchCards_withAuthentication_returnsPageOfCardDto() throws Exception {
        CardDto card = CardDto.builder().build();
        Page<CardDto> page = new PageImpl<>(Collections.singletonList(card), PageRequest.of(0, 5), 1);
        Mockito.when(userCardService.searchCards(
                ArgumentMatchers.any(),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/cards/search")
                        .param("balance", "50.0")
                        .param("isGreater", "false")
                        .param("status", CardStatus.ACTIVE.name())
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void requestCardLock_withAuthentication_returnsCardDto() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardDto dto = CardDto.builder().build();
        Mockito.when(userCardService.requestCardLock(
                eq(cardId),
                any(UUID.class)
        )).thenReturn(dto);

        mockMvc.perform(patch("/api/cards/lock/{card-id}", cardId)
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}