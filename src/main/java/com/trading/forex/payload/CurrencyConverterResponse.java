package com.trading.forex.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class CurrencyConverterResponse {

    private String base;
    private Date date;
    private Map<String, String> rates;

    public CurrencyConverterResponse() {
    }

}
