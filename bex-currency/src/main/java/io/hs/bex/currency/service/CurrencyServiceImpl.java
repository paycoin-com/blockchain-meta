package io.hs.bex.currency.service;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.currency.handler.CryptoCompareHandler;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.CurrencyRateStack;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.task.HourlyXRatesTask;
import io.hs.bex.currency.task.LatestXRatesTask;
import io.hs.bex.currency.utils.CurrencyUtils;
import io.hs.bex.currency.task.DataPublishTask;
import io.hs.bex.currency.task.FiatXRatesTask;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import io.hs.bex.currency.service.api.CurrencyService;
import io.hs.bex.datastore.service.api.DataStoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;


@Service( "CurrencyService" )
public class CurrencyServiceImpl implements CurrencyService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CryptoCompareHandler.class );
    // ---------------------------------

    final int LAST_XRATES_FETCH_PERIOD = 180; // seconds
    final int FIAT_XRATES_FETCH_PERIOD = 600; // seconds
    final int HOURLY_XRATES_FETCH_PERIOD = 1800; // seconds

    public final SysCurrency BASE_SYSTEM_CURRENCY = SysCurrency.USD;

    // Later Change exchange to Binance or Coinbase
    public final String STOCK_EXCHANGE_SOURCE = "";

    final String XRATES_ROOT_FOLDER = "/xrates";

    private List<CurrencyRate> fiatXRates = Collections.synchronizedList( new ArrayList<>() );

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CurrencyTaskManager taskManager;

    @Autowired
    DataStoreService dataStoreService;

    @Autowired
    //@Qualifier( "CoinPaprikaHandler" )
    @Qualifier( "CryptoCompareHandler" )
    CurrencyInfoService digitalCcyService;

    @Autowired
    @Qualifier( "ExchangeRatesAPI" )
    CurrencyInfoService fiatCcyService;

    private CurrencyInfoRequest currencyTaskParams = new CurrencyInfoRequest();

    @PostConstruct
    public void init()
    {
        buildTaskParams();
    }

    private HourlyXRatesTask startHourlyXRatesTask()
    {
        return new HourlyXRatesTask( this );
    }

    private LatestXRatesTask startLatesXRatesTask()
    {
        return new LatestXRatesTask( this );
    }

    private FiatXRatesTask startFiatXRatesTask()
    {
        return new FiatXRatesTask( this );
    }

    private DataPublishTask startDataPublishTask()
    {
        return new DataPublishTask( dataStoreService );
    }

    public void buildTaskParams()
    {
        currencyTaskParams.clearCurrencies();

        currencyTaskParams.getSourceCurrencies().addAll( getSupported( CurrencyType.DIGITAL ) );
        currencyTaskParams.getTargetCurrencies().addAll( getSupported( CurrencyType.FIAT ) );
    }

    @Override
    public void startSyncJob()
    {
        taskManager.startScheduledAtFixed( startFiatXRatesTask(), "FiatXRatesTask", 0, FIAT_XRATES_FETCH_PERIOD );
        taskManager.startScheduledAtFixed( startHourlyXRatesTask(), "HourlyXRatesTask", 30,
                HOURLY_XRATES_FETCH_PERIOD );
        taskManager.startScheduledAtFixed( startLatesXRatesTask(), "LatesXRatesTask", 35, LAST_XRATES_FETCH_PERIOD );
        taskManager.startScheduledTask( startDataPublishTask(), "DataPublishProcessTask", 60, 60 );
    }

    @Override
    public CurrencyInfoService getInfoService()
    {
        return digitalCcyService;
    }

    @Override
    public Set<SysCurrency> getCurrencyList()
    {
        try
        {
            HashSet<SysCurrency> currencies = new HashSet<>( Arrays.asList( SysCurrency.values() ) );

            String data = getFileContent( "", "index.json" );

            if( !Strings.isNullOrEmpty( data ) )
            {
                List<SysCurrency> supported = mapper.readValue( data, new TypeReference<List<SysCurrency>>() {} );
                currencies.addAll( supported );
            }

            return currencies;
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency list", e );
        }

        return Collections.emptySet();
    }

    @Override
    public SysCurrency getCurrencyDetails( String code )
    {
        try
        {
            SysCurrency currency = SysCurrency.find( code );
            String details = getFileContent( "/" + currency.getCode(), "index.json" );
            currency.setDetails( details );

            return currency;
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency details", e );
        }

        return SysCurrency.OTHER;
    }

    @Override
    public List<SysCurrency> getSupported( CurrencyType currencyType )
    {
        try
        {
            String data = getFileContent( "", "index.json" );
            List<SysCurrency> supported = mapper.readValue( data, new TypeReference<List<SysCurrency>>() {} );

            if( CurrencyType.OTHER != currencyType )
            {
                return supported.stream().filter( c -> c.getType() == currencyType ).collect( Collectors.toList() );
            }

            return supported;
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency list (supported:)", e );
        }

        return Collections.emptyList();
    }

    @Override
    public void setSupported( String[] supported )
    {
        try
        {
            HashSet<SysCurrency> currencies = new HashSet<>();

            for( String code: supported )
            {
                currencies.add( SysCurrency.find( code ) );
            }

            saveFile( "", "index.json", mapper.writeValueAsString( currencies ) );

            // -----------------------
            buildTaskParams();
            // -----------------------
        }
        catch( Exception e )
        {
            logger.error( "Error setting supporeted currences", e );
        }

    }

    @Override
    public void updateCurrency( String code, String details )
    {
        try
        {
            SysCurrency currency = SysCurrency.find( code );
            saveFile( "/" + currency.getCode(), "index.json", details );
        }
        catch( Exception e )
        {
            logger.error( "Error updating  currencies", e );
        }
    }

    @Override
    public void savePeriodicXRates( CurrencyInfoRequest request )
    {
        saveXRates( request, currencyTaskParams.getTargetCurrencies() );
    }

    @Override
    public void saveXRates( CurrencyInfoRequest request )
    {
        saveXRates( request, request.getTargetCurrencies() );
    }

    @Override
    public void saveXRates( CurrencyInfoRequest request, List<SysCurrency> targetCurrencies )
    {
        try
        {
            String path = "";
            List<CurrencyRate> baseXRates, xrates;
            Map<String, Float> dataMap = new LinkedHashMap<>();

            for( SysCurrency sourceCurrency: request.getSourceCurrencies() )
            {
                LocalDateTime lastDate = null, localDateTime = null;
                dataMap.clear();

                baseXRates = digitalCcyService
                        .getXRatesBy( new CurrencyInfoRequest( sourceCurrency, BASE_SYSTEM_CURRENCY,
                                request.getPeriod(), request.getDateTo(), request.getLimit(), STOCK_EXCHANGE_SOURCE ) );

                for( SysCurrency targetCurrency: targetCurrencies )
                {
                    String rootPath = "/" + sourceCurrency.getCode() + "/" + targetCurrency.getCode() + "/";

                    xrates = calculateXRateDetails( request, baseXRates, targetCurrency );

                    int hour = -100;

                    for( CurrencyRate xrate: xrates )
                    {
                        localDateTime = xrate.getLocalDateTime();

                        if( request.getPeriod() == TimePeriod.MINUTE )
                        {
                            if( hour != localDateTime.getHour() )
                            {
                                if( dataMap.size() > 0 )
                                {
                                    path = CurrencyUtils.buildDirStructure( TimePeriod.HOUR, rootPath, lastDate );

                                    appendData( path, "index.json", dataMap );
                                    dataMap.clear();
                                }
                            }

                            dataMap.put( String.format( "%02d", localDateTime.getMinute() ), xrate.getRate() );
                        }
                        else
                        {
                            path = CurrencyUtils.buildDirStructure( request.getPeriod(), rootPath, localDateTime );
                            saveFile( path, "index.json", Float.toString( xrate.getRate() ) );
                        }

                        hour = localDateTime.getHour();
                        lastDate = localDateTime;
                    }

                    if( dataMap.size() > 0 )
                    {
                        path = CurrencyUtils.buildDirStructure( TimePeriod.HOUR, rootPath, lastDate );
                        appendData( path, "index.json", dataMap );
                        dataMap.clear();
                    }
                }

                // ------------------------------------------------------------------------
                logger.info( "Successfully fetchAndStore XRates for:{}-{}", sourceCurrency.getCode(),
                        StringUtils.instantToString( request.getDateTo() ) );
                // ------------------------------------------------------------------------
            }
        }
        catch( Exception e )
        {
            logger.error( "Error saving xrates for:{}", request, e );
        }
    }

    private void appendData( String path, String fileName, Map<String, Float> dataMap ) throws IOException
    {
        LinkedHashMap<String, Float> contentMap = null;

        String content = getFileContent( path, fileName );

        if( !Strings.isNullOrEmpty( content ) )
        {
            contentMap = mapper.readValue( content, new TypeReference<LinkedHashMap<String, Float>>() {} );
            contentMap.putAll( dataMap );
            saveFile( path, fileName, mapper.writeValueAsString( contentMap ) );
        }
        else
            saveFile( path, fileName, mapper.writeValueAsString( dataMap ) );
    }

    @Override
    public void saveLatestXRates( CurrencyInfoRequest request )
    {
        try
        {
            List<CurrencyRate> xrates = digitalCcyService.getLatestXRates( request );

            // ---------- Add other currency rates ----------
            List<CurrencyRateStack> rateStockList = calculateXRateDetails( xrates );
            // ----------------------------------------------

            for( CurrencyRateStack rateStack: rateStockList )
            {
                String rootPath = "/" + rateStack.getCurrency().getCode() + "/";
                saveFile( rootPath, "index.json", mapper.writeValueAsString( rateStack ) );
            }
        }
        catch( Exception e )
        {
            logger.error( "Error saving latest xrates for:{}", request, e );
        }
    }

    @Override
    public void fetchAndStoreXRates( int storeType, TimePeriod timePeriod, int fetchSize )
    {
        CurrencyInfoRequest request = new CurrencyInfoRequest();

        request.setXStockSource( STOCK_EXCHANGE_SOURCE );
        request.getSourceCurrencies().addAll( currencyTaskParams.getSourceCurrencies() );
        request.getTargetCurrencies().add( BASE_SYSTEM_CURRENCY );
        request.setPeriod( timePeriod );
        request.setDateTo( Instant.now() );
        request.setLimit( fetchSize );

        if( storeType == 1 )
            savePeriodicXRates( request );
        else
            saveLatestXRates( request );
    }

    @Override
    public List<CurrencyRate> getXRates( CurrencyInfoRequest request )
    {

        if( request.getCurrencyType() == CurrencyType.FIAT )
        {
            return fiatCcyService.getXRatesBy( request );
        }
        else
        {
            return digitalCcyService.getXRatesBy( request );
        }
    }

    @Override
    public List<CurrencyRate> getLatestXRates( CurrencyInfoRequest request )
    {

        if( request.getCurrencyType() == CurrencyType.FIAT )
        {
            request.getSourceCurrencies().add( BASE_SYSTEM_CURRENCY );
            request.getTargetCurrencies().addAll( currencyTaskParams.getTargetCurrencies() );
            request.getTargetCurrencies().remove( BASE_SYSTEM_CURRENCY );

            List<CurrencyRate> tempXRates = fiatCcyService.getLatestXRates( request );
            fiatXRates.clear();
            fiatXRates.add( new CurrencyRate( BASE_SYSTEM_CURRENCY,BASE_SYSTEM_CURRENCY, 1 ));
            fiatXRates.addAll( tempXRates );

            return fiatXRates;
        }
        else
        {
            return digitalCcyService.getLatestXRates( request );
        }
    }

    private List<CurrencyRateStack> calculateXRateDetails( List<CurrencyRate> digitalCcyXrates )
    {
        CurrencyRateStack xrateStack = null;
        
        List<CurrencyRateStack> xrateStackList = new ArrayList<>();

        for( CurrencyRate fiatXRate: fiatXRates )
        {
            xrateStack = new CurrencyRateStack();
            xrateStack.setCurrency( fiatXRate.getTargetCurrency() );
            xrateStackList.add( xrateStack );
            
            for( CurrencyRate digXRate: digitalCcyXrates )
            {
                if(fiatXRate.getCurrency() ==  digXRate.getTargetCurrency()) 
                {
                    xrateStack.setTime( digXRate.getDate());
                    xrateStack.getRates().put( digXRate.getCurrency(), digXRate.getRate() * fiatXRate.getRate() );
                }
            }                
        }
            

        return xrateStackList;
    }

    private List<CurrencyRate> calculateXRateDetails( CurrencyInfoRequest request, List<CurrencyRate> digitalCcyXrates,
            SysCurrency targetCurrency )
    {

        List<CurrencyRate> xrateDetails = new ArrayList<>();

        if( targetCurrency == BASE_SYSTEM_CURRENCY )
        {
            xrateDetails.addAll( digitalCcyXrates );
        }
        else
        {
            List<CurrencyRate> localFiatXRates = null;
            Instant lastDate = null;

            localFiatXRates = fiatXRates;

            CurrencyRate fiatXRate = localFiatXRates.stream()
                    .filter( xRate -> xRate.getTargetCurrency() == targetCurrency ).findAny().orElse( null );

            for( CurrencyRate digXRate: digitalCcyXrates )
            {
                if( digXRate.getTargetCurrency() == BASE_SYSTEM_CURRENCY )
                {
                    if( !request.isFastCalculation() )
                    {
                        if( lastDate == null || ( ChronoUnit.DAYS.between( lastDate, digXRate.getDate() ) >= 1) )
                        {
                            localFiatXRates = fiatCcyService.getXRatesBy( new CurrencyInfoRequest( BASE_SYSTEM_CURRENCY,
                                    targetCurrency, request.getPeriod(), request.getDateTo(), request.getLimit() ) );

                            fiatXRate = localFiatXRates.stream()
                                    .filter( xRate -> xRate.getTargetCurrency() == targetCurrency ).findAny()
                                    .orElse( null );
                        }
                    }

                    if( fiatXRate != null )
                    {
                        xrateDetails.add( new CurrencyRate( digXRate.getDate(), digXRate.getCurrency(),
                                fiatXRate.getTargetCurrency(), digXRate.getRate() * fiatXRate.getRate() ) );
                    }

                    lastDate = digXRate.getDate();
                }
            }
        }

        return xrateDetails;
    }

    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException
    {
        dataStoreService.saveFile( true, XRATES_ROOT_FOLDER + path, "index.json", value );
    }

    private String getFileContent( String path, String fileName )
    {
        return dataStoreService.getFileContent( XRATES_ROOT_FOLDER + path, "index.json" );
    }

    public void setFiatXRates( List<CurrencyRate> fiatXRates )
    {
        this.fiatXRates = fiatXRates;
    }
   
}
