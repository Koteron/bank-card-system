package com.example.bankcards.service.currency;

import com.example.bankcards.entity.util.Currency;

import java.math.BigDecimal;

/**
 * Service interface for currency conversion operations.
 */
public interface CurrencyService {

    /**
     * Converts a given amount from one currency to another using predefined exchange rates.
     *
     * @param amount       the amount to convert
     * @param fromCurrency the source currency
     * @param toCurrency   the target currency
     * @return the converted amount in the target currency, rounded to 2 decimal places
     * @throws IllegalArgumentException if the conversion rate is not defined
     */
    BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency);
}
