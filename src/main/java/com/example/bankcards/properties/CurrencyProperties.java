package com.example.bankcards.properties;

import com.example.bankcards.entity.util.Currency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "app.currencies")
@Getter
@Setter
public class CurrencyProperties {
    private String defaultCurrency;
    private Map<Currency, Map<Currency, BigDecimal>> rates;
}
