package io.hs.bex.currency.service.api;

import java.util.List;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;

public interface CurrencyInfoService
{
    CurrencyRate getCurrencyRate( String sourceCurrency, String targetCurrency );

    List<CurrencyRate> getCurrencyRateBy( CurrencyInfoRequest request );

    List<CurrencyRate> getCurrentXRates( CurrencyInfoRequest request );
}
