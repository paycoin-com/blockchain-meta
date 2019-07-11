package io.hs.bex.currency.service.stats;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import io.hs.bex.currency.model.CurrencyRateStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.stats.StatsRates;
import io.hs.bex.currency.model.stats.StatsType;
import io.hs.bex.datastore.service.api.DataStoreService;


/**
 * @author nisakov Statistics management service
 */
@Service( "CurrencyStatsService" ) public class CurrencyStatsService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CurrencyStatsService.class );
    // ---------------------------------

    private final String STATS_ROOT_FOLDER = "/xrates/stats/";
    private final String XRATES_ROOT_FOLDER = "/xrates/";

    @Autowired
    private DataStoreService dataStoreService;

    @Autowired
    private StatsTracker statsTracker;

    @Autowired
    private ObjectMapper mapper;

    private List<SysCurrency> fiatCurrencies;
    private List<SysCurrency> digCurrencies;

    public void init(List<SysCurrency> fiatCurrencies, List<SysCurrency> digCurrencies)
    {
        this.fiatCurrencies = fiatCurrencies;
        this.digCurrencies = digCurrencies;
    }

    @Async
    public void updateStatsDataAsync(List<CurrencyRateStack> rateStackList, Instant timestamp)
    {
        updateStatsData(rateStackList, timestamp );
    }

    public void updateStatsData(List<CurrencyRateStack> rateStackList, Instant timestamp)
    {
        try
        {
            List<StatsType> statsTypeList = statsTracker.requiredUpdates( timestamp );

            for ( StatsType statsType : statsTypeList )
            {
                for ( CurrencyRateStack rateStack : rateStackList )
                {
                    for ( SysCurrency coin : rateStack.getRates().keySet() )
                    {
                        updateStatsData( statsType, rateStack.getCurrencyStr(), coin.getCode(),
                                rateStack.getRates().get( coin ), rateStack.getTime() );
                    }
                }
            }

            logger.info( " (!!!)  **** Stats data update ended. ***** " );

        }
        catch ( Exception e )
        {
            logger.error( "Error when updating stats data:", e );
        }
    }

    @Async
    public void createStatsDataAsync()
    {
        createStatsData();
    }

    public void createStatsData()
    {
        Instant timestamp = Instant.now();

        try
        {
            for ( SysCurrency fCcy : fiatCurrencies )
            {
                for ( SysCurrency dCcy : digCurrencies )
                {
                    for ( StatsType statsType : StatsType.values() )
                    {
                        saveStatsData( statsType, fCcy.getCode(), dCcy.getCode(), timestamp );
                    }
                }
            }

            logger.info( " (!!!)  **** Stats data creation ended. ***** " );

        }
        catch ( Exception e )
        {
            logger.error( "Error creating stats data:", e );
        }
    }

    private void updateStatsData(StatsType statsType, String fiatCurrency, String digCurrency, String rate,
            Instant timestamp) throws IOException
    {
        String fileName = statsType.name().toLowerCase() + ".json";

        StatsRates statsRates = getStatsData( statsType, fiatCurrency, digCurrency, timestamp, fileName );

        statsRates.getRates().add( rate );
        statsRates.setTimestamp( timestamp.getEpochSecond() );

        //-------- Adjust size of the object --------------------
        adjustStatsDataSize( statsType, statsRates );
        //-------------------------------------------------------

        saveFile( fiatCurrency + "/" + digCurrency, fileName, mapper.writeValueAsString( statsRates ) );

        logger.info( "Successfully created stats for: {}:{}", fiatCurrency, digCurrency );

    }

    private StatsRates getStatsData(StatsType statsType, String fiatCurrency, String digCurrency, Instant timestamp,
            String fileName) throws IOException
    {

        String outJson = getFileContent( fiatCurrency + "/" + digCurrency, fileName );

        if ( !Strings.isNullOrEmpty( outJson ) )
        {
            StatsRates statRates = mapper.readValue( outJson, StatsRates.class );

            if ( statRates != null )
            {
                return statRates;
            }
        }

        return new StatsRates( timestamp.getEpochSecond(), statsType );

    }

    private void adjustStatsDataSize(StatsType statsType, StatsRates statsRates)
    {
        if ( statsRates.getRates().size() > statsType.getRecordCount() )
            statsRates.getRates().remove( 0 );
    }

    private void saveStatsData(StatsType statsType, String fiatCurrency, String digCurrency, Instant timestamp)
            throws IOException
    {
        String fileName = statsType.name().toLowerCase() + ".json";

        LocalDateTime ldt = LocalDateTime.ofInstant( timestamp, ZoneId.systemDefault() );
        StatsRates statsRates = new StatsRates( timestamp.getEpochSecond(), statsType );

        for ( int x = 0; x <= statsType.getRecordCount(); x++ )
        {
            String rateValue = getHistoricalXRates( fiatCurrency, digCurrency, ldt );

            if ( !Strings.isNullOrEmpty( rateValue ) )
                statsRates.getRates().add( rateValue );

            ldt = adjustDateTime( statsType, ldt );
        }

        Collections.reverse( statsRates.getRates() );

        saveFile( fiatCurrency + "/" + digCurrency, fileName, mapper.writeValueAsString( statsRates ) );

        logger.info( "Successfully created stats for: {}:{}", fiatCurrency, digCurrency );

    }

    private String getHistoricalXRates(String fiatCurrency, String digCurrency, LocalDateTime ldt) throws IOException
    {
        String rateValue = "";

        int year = ldt.getYear();
        String month = String.format( "%02d", ldt.getMonthValue() );
        String day = String.format( "%02d", ldt.getDayOfMonth() );
        String hour = String.format( "%02d", ldt.getHour() );

        String pathDay = "historical/" + digCurrency + "/" + fiatCurrency + "/" + year + "/" + month + "/" + day;
        String jsonValue = dataStoreService.getFileContent( XRATES_ROOT_FOLDER + pathDay + "/" + hour, "index.json" );

        if ( !Strings.isNullOrEmpty( jsonValue ) )
        {
            TreeMap<String, String> rates = mapper.readValue( jsonValue, new TypeReference<TreeMap<String, String>>()
            {
            } );
            rateValue = rates.lastEntry().getValue();
        }
        else
        {
            jsonValue = dataStoreService.getFileContent( XRATES_ROOT_FOLDER + pathDay, "index.json" );

            if ( !Strings.isNullOrEmpty( jsonValue ) )
                rateValue = jsonValue.trim().replaceAll( "\"", "" );
        }

        return rateValue;
    }

    private LocalDateTime adjustDateTime(StatsType statsType, LocalDateTime dateTime)
    {
        return dateTime.minus( statsType.getScale(), statsType.getScaleUnit() );
    }

    private void saveFile(String path, String fileName, String value)
    {
        dataStoreService.saveFile( true, STATS_ROOT_FOLDER + path, fileName, value );
    }

    private String getFileContent(String path, String fileName)
    {
        return dataStoreService.getFileContent( STATS_ROOT_FOLDER + path, fileName );
    }

}
