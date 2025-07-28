package com.example.bankcards.specification;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class CardSpecification {

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Card> hasBalance(Double balance, Boolean isGreater) {
        return (root, query, cb) -> {
            if (balance == null) {
                return cb.conjunction();
            }

            BigDecimal bd = BigDecimal.valueOf(balance);

            if (isGreater == null || isGreater) {
                return cb.greaterThanOrEqualTo(root.get("balance"), bd);
            } else {
                return cb.lessThanOrEqualTo(root.get("balance"), bd);
            }
        };
    }

    public static Specification<Card> hasOwnerId(UUID ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("owner").get("id"), ownerId);
        };
    }
}
