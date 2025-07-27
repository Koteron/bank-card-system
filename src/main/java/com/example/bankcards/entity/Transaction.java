package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@Table(name = "transactions")
@RequiredArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private double amount;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "card_to", referencedColumnName = "id")
    private Card card_to;

    @ManyToOne
    @JoinColumn(name = "card_to", referencedColumnName = "id")
    private Card card_from;
}
