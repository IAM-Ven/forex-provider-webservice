package com.trading.forex.payload;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ExchangeRatesResponse {
    private Date date;
    private Map<String, BigDecimal> rates;
}
