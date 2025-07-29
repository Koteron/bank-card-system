package com.example.bankcards.controller.card;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.card.AdminCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AdminCardController.class)
@Import(TestSecurityConfig.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminCardService adminCardService;

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
    void searchCards_returnsPageOfCardDto() throws Exception {
        CardDto card = CardDto.builder().build();
        Page<CardDto> page = new PageImpl<>(Collections.singletonList(card),
                PageRequest.of(0,5),1);
        Mockito.when(adminCardService.searchCards(any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/cards/search")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .param("balance", "100.0")
                        .param("isGreater", "true")
                        .param("ownerId", UUID.randomUUID().toString())
                        .param("status", CardStatus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getCardById_returnsCardDto() throws Exception {
        UUID id = UUID.randomUUID();
        CardDto dto = CardDto.builder().build();
        Mockito.when(adminCardService.getCardById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/admin/cards/get/{id}", id)
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCard_returnsCreated() throws Exception {
        CardCreationDto creation = new CardCreationDto(UUID.randomUUID(), Currency.USD);
        CardDto dto = CardDto.builder().build();
        Mockito.when(adminCardService.createCard(any(CardCreationDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/admin/cards/create")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creation)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCard_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/cards/delete/{id}", id)
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        Mockito.verify(adminCardService).deleteCardById(id);
    }

    @Test
    void updateCardStatus_returnsUpdatedDto() throws Exception {
        CardStatusUpdateDto statusDto = new CardStatusUpdateDto(UUID.randomUUID(), CardStatus.LOCKED);
        CardDto dto = CardDto.builder().build();
        Mockito.when(adminCardService.updateCardStatus(any(CardStatusUpdateDto.class))).thenReturn(dto);

        mockMvc.perform(patch("/api/admin/cards/status")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
