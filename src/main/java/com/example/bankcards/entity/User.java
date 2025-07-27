package com.example.bankcards.entity;

import com.example.bankcards.entity.util.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@RequiredArgsConstructor
@DynamicInsert
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotNull
    private String nickname;

    @NotNull
    private String email;

    @NotNull
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
