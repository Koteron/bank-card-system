package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    boolean existsByEncryptedNumber(String encryptedNumber);

    Optional<Card> findByEncryptedNumber(String encryptedNumber);

    @Override
    @EntityGraph(attributePaths = {"owner"})
    @NonNull
    Page<Card> findAll(@NonNull Specification<Card> specification, @NonNull Pageable pageable);

    List<Card> findAllByExpirationDateBeforeAndStatusNot(LocalDate date, CardStatus status);
}
