package com.example.bankcards.controller.transaction;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.entity.util.UserRole;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.transaction.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TransactionController.class)
@Import(TestSecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

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
    void transfer_withAuthentication_returnsCardDto() throws Exception {
        TransferRequestDto request = new TransferRequestDto(
                UUID.randomUUID(), null,
                UUID.randomUUID(), null,
                new BigDecimal("10.00")
        );
        CardDto dto = CardDto.builder().build();
        Mockito.when(transactionService.transfer(
                eq(testUser), any(TransferRequestDto.class)
        )).thenReturn(dto);

        mockMvc.perform(post("/api/transactions/transfer")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deposit_withAuthentication_returnsCardDto() throws Exception {
        OneCardOperationDto request = new OneCardOperationDto(
                null,
                "1234-1234-1234-1234",
                Currency.USD,
                new BigDecimal("25.00")
        );
        CardDto dto = CardDto.builder().build();
        Mockito.when(transactionService.deposit(
                eq(testUser), any(OneCardOperationDto.class)
        )).thenReturn(dto);

        mockMvc.perform(post("/api/transactions/deposit")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void withdraw_withAuthentication_returnsCardDto() throws Exception {
        OneCardOperationDto request = new OneCardOperationDto(
                UUID.randomUUID(),
                null,
                Currency.EUR,
                new BigDecimal("15.00")
        );
        CardDto dto = CardDto.builder().build();
        Mockito.when(transactionService.withdraw(
                eq(testUser), any(OneCardOperationDto.class)
        )).thenReturn(dto);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .with(user(new UserDetailsImpl(testUser)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
