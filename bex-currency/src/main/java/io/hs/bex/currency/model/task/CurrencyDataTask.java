package io.hs.bex.currency.model.task;

import java.time.Instant;
import java.util.List;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.service.api.CurrencyService;

public class CurrencyDataTask implements Runnable
{
    private CurrencyService currencyService;
    
    private CurrencyInfoRequest request = null;
    
    private List<CurrencyInfoRequest> requests = null;
    
    public CurrencyDataTask( CurrencyService currencyService, CurrencyInfoRequest request ) 
    {
        this.request = request;
        this.currencyService = currencyService;
    }
    
    public CurrencyDataTask( CurrencyService currencyService, List<CurrencyInfoRequest> requests ) 
    {
        this.requests = requests;
        this.currencyService = currencyService;
    }

    @Override
    public void run()
    {
        Instant requestDate = Instant.now();
        
        if( request != null ) 
        {
            request.setDateTo( requestDate );
            request.setLimit( 2 );
            currencyService.saveCurrencyRates( request, true );
        }
        else if( requests != null )
        {
            for( CurrencyInfoRequest request:requests ) 
            {
                request.setDateTo( requestDate );
                request.setLimit( 2 );
                currencyService.saveCurrencyRates( request, true );
            }
        }
    }
}
