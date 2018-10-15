package io.hs.bex.web.controller.currency;

import java.util.List;
import java.util.NoSuchElementException;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.service.api.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( value = "/api/v1/currency/" )
public class CurrencyRestController
{
    // ---------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger( CurrencyRestController.class );
    // ---------------------------------
    
    @Autowired
    CurrencyService currencyService;
    
    /**
     * Get the publihser by AppID
     * 
     * @return
     */
    @RequestMapping( value = "exchangerates", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> get( @RequestParam( required = false, name = "period" ) String period )
    {
        try
        {
            LOGGER.info( " *** Get RestRequest currency/rate?period = ", period );
            
            return new ResponseEntity<List<CurrencyRate>>( currencyService.getInfoService()
                    .getCurrencyRateBy( new CurrencyInfoRequest() ), HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error getting currency rates by period:{}", period, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    
    @RequestMapping( value = "xrates/save/{scurrency}/{tcurrency}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> saveXRates( @PathVariable("scurrency") String sourceCurrency,
                                         @PathVariable("tcurrency") String targetCurrency,
                                         @RequestParam( required = false, name = "period" ) String period,
                                         @RequestParam( required = false, name = "to_date" ) String toDate,
                                         @RequestParam( required = false, name = "limit" ) int limit )
    {
        try
        {
            LOGGER.info( " *** Get RestRequest currency/xrates/save?period = ", period );
            
            CurrencyInfoRequest request = new CurrencyInfoRequest(sourceCurrency, targetCurrency, period,toDate, limit);
            
            currencyService.saveCurrencyRates( request, false );
            
            return new ResponseEntity<>( HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error saving currency rates by period:{}", period, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    @RequestMapping( value = "xrates/{scurrency}/{tcurrency}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getXRates( @PathVariable("scurrency") String sourceCurrency,
                                         @PathVariable("tcurrency") String targetCurrency,
                                         @RequestParam( required = false, name = "period" ) String period,
                                         @RequestParam( required = false, name = "to_date" ) String toDate,
                                         @RequestParam( required = false, name = "limit" ) int limit )
    {
        try
        {
            LOGGER.info( " *** Get RestRequest currency/xrates/?period={}&to_date={}&limit={}", period, toDate, limit );
            
            CurrencyInfoRequest request = new CurrencyInfoRequest(sourceCurrency, targetCurrency, period,toDate, limit);
            
            return new ResponseEntity<List<CurrencyRate>>( currencyService.getInfoService().getCurrencyRateBy( request ), 
                    HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error getting currency rates by period:{}", period, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

}
