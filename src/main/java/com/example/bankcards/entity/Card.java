package com.example.bankcards.entity;

import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.entity.util.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "encrypted_number", nullable = false)
    @NotNull
    private String encryptedNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    @NotNull
    private User owner;

    @Column(name = "expiration_date", nullable = false)
    @NotNull
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private CardStatus status;

    @NotNull
    @Column(nullable = false)
    @Digits(integer = 19, fraction = 2)
    private BigDecimal balance;
}