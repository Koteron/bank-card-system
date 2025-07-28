package com.example.bankcards.service.currency;

import com.example.bankcards.entity.util.Currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency);
}
