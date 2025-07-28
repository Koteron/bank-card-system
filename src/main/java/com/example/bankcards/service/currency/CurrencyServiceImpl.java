package com.example.bankcards.service.currency;

import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.properties.CurrencyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyProperties currencyProperties;

    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal rate = currencyProperties.getRates()
                .getOrDefault(fromCurrency, Map.of())
                .get(toCurrency);

        if (rate == null) {
            throw new IllegalArgumentException("Rate not found");
        }

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
