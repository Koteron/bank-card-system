package com.example.bankcards.service.currency;

import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.properties.CurrencyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyProperties currencyProperties;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void convert_sameCurrency_returnsOriginalAmount() {
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.USD;

        BigDecimal result = currencyService.convert(amount, currency, currency);

        assertEquals(amount, result);
    }

    @Test
    void convert_rateExists_returnsConvertedAmount() {
        BigDecimal amount = new BigDecimal("50.00");
        Currency from = Currency.USD;
        Currency to = Currency.EUR;
        BigDecimal rate = new BigDecimal("0.85");

        when(currencyProperties.getRates()).thenReturn(
                Map.of(from, Map.of(to, rate))
        );

        BigDecimal result = currencyService.convert(amount, from, to);

        assertEquals(new BigDecimal("42.50"), result);
        verify(currencyProperties).getRates();
    }

    @Test
    void convert_rateMissing_throwsIllegalArgumentException() {
        BigDecimal amount = new BigDecimal("10.00");
        Currency from = Currency.USD;
        Currency to = Currency.RUB;

        when(currencyProperties.getRates()).thenReturn(
                Map.of(from, Map.of())
        );

        assertThrows(IllegalArgumentException.class,
                () -> currencyService.convert(amount, from, to));
        verify(currencyProperties).getRates();
    }
}
