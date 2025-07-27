package com.example.bankcards.entity;

import com.example.bankcards.entity.util.CardStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@Table(name = "cards")
@RequiredArgsConstructor
public class Card {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "encrypted_number")
    private String encryptedNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private double balance;
}