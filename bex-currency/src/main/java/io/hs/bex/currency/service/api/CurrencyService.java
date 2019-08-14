package io.hs.bex.currency.service.api;

import java.util.List;
import java.util.Set;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.model.stats.CoinInfo;


public interface CurrencyService
{

    CurrencyInfoService getInfoService();

    Set<SysCurrency> getCurrencyList();

    void setSupported( String[] supported );

    List<SysCurrency> getSupported( CurrencyType currencyType );

    void startSyncJob();

    SysCurrency getCurrencyDetails( String code );

    void updateCurrency( String code, String details );

    void fetchAndStoreXRates( int storeType, TimePeriod timePeriod, int fetchSize );

    void saveXRates( CurrencyInfoRequest request );

    void fetchAndStoreCoinInfo();

    void saveCoinInfo(CoinInfo coinInfo);

    List<CurrencyRate> getXRates( CurrencyInfoRequest request );

    void saveLatestXRates( CurrencyInfoRequest request );

    List<CurrencyRate> getLatestXRates( CurrencyInfoRequest request );

    void savePeriodicXRates( CurrencyInfoRequest request );

    void saveXRates( CurrencyInfoRequest request, List<SysCurrency> targetCurrencies );

    void createStatsDataAsync();

}
