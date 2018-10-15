package io.hs.bex.currency.service.api;

import java.util.List;
import java.util.Set;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.model.SysCurrency;

public interface CurrencyService
{

    CurrencyInfoService getInfoService();

    void saveCurrencyRates( CurrencyInfoRequest request, boolean storeLastRate );

    Set<SysCurrency> getCurrencyList();

    void setSupported( String[] supported );

    List<SysCurrency> getSupported( CurrencyType currencyType );

    void startSyncJob();

}
