package io.hs.bex.currency.task;


import io.hs.bex.currency.service.api.CurrencyService;


public class CoinInfoTask implements Runnable
{
    private CurrencyService currencyService;

    public CoinInfoTask( CurrencyService currencyService )
    {
        this.currencyService = currencyService;
    }

    @Override
    public void run()
    {
        currencyService.fetchAndStoreCoinInfo();
    }
}
