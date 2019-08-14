package io.hs.bex.currency.service.api;

import java.util.List;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.stats.CoinInfo;
import io.hs.bex.currency.task.CoinInfoTask;


public interface CurrencyInfoService
{
    CurrencyRate getXRate( String sourceCurrency, String targetCurrency );

    List<CurrencyRate> getXRatesBy( CurrencyInfoRequest request );

    List<CurrencyRate> getLatestXRates( CurrencyInfoRequest request );

    CoinInfo getCoinInfo( CurrencyInfoRequest request );

}
