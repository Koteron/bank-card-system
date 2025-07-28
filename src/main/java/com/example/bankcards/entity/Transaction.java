package com.example.bankcards.entity;

import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.entity.util.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotNull
    @Column(nullable = false)
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "card_to", referencedColumnName = "id")
    private Card cardTo;

    @ManyToOne
    @JoinColumn(name = "card_from", referencedColumnName = "id")
    private Card cardFrom;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "currency_to", nullable = false)
    private Currency currencyTo;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "currency_from", nullable = false)
    private Currency currencyFrom;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TransactionType type;
}
