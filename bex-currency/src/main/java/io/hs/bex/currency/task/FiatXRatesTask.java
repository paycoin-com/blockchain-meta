package io.hs.bex.currency.task;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.service.api.CurrencyService;

public class FiatXRatesTask implements Runnable
{
    private CurrencyService currencyService;
    
    public FiatXRatesTask( CurrencyService currencyService ) 
    {
        this.currencyService = currencyService;
    }

    @Override
    public void run()
    {
        currencyService.getLatestXRates( new CurrencyInfoRequest( CurrencyType.FIAT )); 
    }
}
