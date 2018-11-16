package io.hs.bex.currency.task;

import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.service.api.CurrencyService;

public class HourlyXRatesTask implements Runnable
{
    private CurrencyService currencyService;
    
    public HourlyXRatesTask( CurrencyService currencyService ) 
    {
        this.currencyService = currencyService;
    }
    
    @Override
    public void run()
    {
        currencyService.fetchAndStoreXRates( 1, TimePeriod.MINUTE, 10 );
    }
}
