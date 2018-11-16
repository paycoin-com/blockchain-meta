package io.hs.bex.currency.task;

import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.service.api.CurrencyService;

public class LatestXRatesTask implements Runnable
{
    private CurrencyService currencyService;
    
    public LatestXRatesTask( CurrencyService currencyService ) 
    {
        this.currencyService = currencyService;
    }

    @Override
    public void run()
    {
        currencyService.fetchAndStoreXRates( 2, TimePeriod.MINUTE, 1 );
    }
}
