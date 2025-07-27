package com.example.bankcards.specification;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.CardStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CardSpecification {
    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (status == null) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Card> hasBalance(Double balance, Boolean isGreater) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (balance == null) return null;
            if (isGreater == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("number"), balance);
            return isGreater ? criteriaBuilder.greaterThanOrEqualTo(root.get("number"), balance)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("number"), balance);
        };
    }

    public static Specification<Card> hasOwnerId(UUID ownerId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (ownerId == null) return null;
            Join<Card, User> userJoin = root.join("owner");
            return criteriaBuilder.equal(userJoin.get("id"), ownerId);
        };
    }
}
