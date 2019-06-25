package io.hs.bex.currency.service;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.stats.Stats;
import io.hs.bex.currency.model.stats.StatsRates;
import io.hs.bex.currency.model.stats.StatsTrackerData;
import io.hs.bex.currency.model.stats.StatsType;
import io.hs.bex.datastore.service.api.DataStoreService;


/**
 * 
 * @author nisakov Statistics management service
 */
@Service( "CurrencyStatsService" )
public class CurrencyStatsService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CurrencyStatsService.class );
    // ---------------------------------

    final String STATS_ROOT_FOLDER = "/xrates/stats/";
    final String XRATES_ROOT_FOLDER = "/xrates/";

    @Autowired
    DataStoreService dataStoreService;

    @Autowired
    StatsTracker tracker;

    @Autowired
    private ObjectMapper mapper;

    List<SysCurrency> fiatCurrencies;
    List<SysCurrency> digCurrencies;

    public void init( List<SysCurrency> fiatCurrencies, List<SysCurrency> digCurrencies )
    {
        this.fiatCurrencies = fiatCurrencies;
        this.digCurrencies = digCurrencies;
    }

    public void setStatsData( Stats stats )
    {
        try
        {
            boolean saveData = false;

            StatsTrackerData currentTrackerData = StatsTrackerData.getTrackerData( stats.getTimeStamp() );

            if( tracker.getTrackerData().getTimestamp() == stats.getTimeStamp() )
                saveData = true;

            if( saveData || tracker.getTrackerData().getLastHour() != currentTrackerData.getLastHour() )
            {
                stats.setType( StatsType.HOURLY );
                saveStatsData( currentTrackerData, stats );

                if( saveData || tracker.getTrackerData().getLastDay() != currentTrackerData.getLastDay() )
                {
                    stats.setType( StatsType.DAILY );
                    saveStatsData( currentTrackerData, stats );
                }

                if( saveData || tracker.getTrackerData().getLastMonth() != currentTrackerData.getLastMonth() )
                {
                    stats.setType( StatsType.MONTHLY );
                    saveStatsData( currentTrackerData, stats );
                }
            }

            tracker.updateTrackerData( currentTrackerData );
        }
        catch( Exception e )
        {
            logger.error( "Error when creating stats data:", e );
        }
    }

    private void saveStatsData( StatsTrackerData currentTrackerData, Stats stats )
            throws JsonParseException, JsonMappingException, IOException
    {
        StatsRates statsRates = null;
        String path = stats.getFiatCurrency().getCode() + "/" + stats.getDigCurrency().getCode();
        String fileName = stats.getType().name().toLowerCase() + ".json";

        String jsonData = getFileContent( path, fileName );

        if( !Strings.isNullOrEmpty( jsonData ) )
        {
            statsRates = mapper.readValue( jsonData, StatsRates.class );
        }
        else
            statsRates = new StatsRates();
        // -------------------------------

        statsRates.setTimestamp( currentTrackerData.getTimestamp().getEpochSecond() );
        statsRates.getRates().add( StringUtils.floatToString( stats.getRate() ) );

        saveFile( path, fileName, mapper.writeValueAsString( statsRates ) );

        logger.info( "Saving stats data for:{},{} for type: {}", stats.getFiatCurrency(), stats.getDigCurrency(),
                stats.getType() );
    }

    public void createStats()
    {
        Instant timestamp = Instant.now();

        try
        {
            for( SysCurrency fCcy: fiatCurrencies )
            {
                for( SysCurrency dCcy: digCurrencies )
                {
                    // --------------- Hourly -----------------------
                    saveHistoricalRates( StatsType.HOURLY, fCcy.getCode(), dCcy.getCode(), timestamp );
                    // -------------------------------------------------
                }
            }
        }
        catch( Exception e )
        {
            logger.error( "Error creating stats data:", e );
        }
    }

    private void saveHistoricalRates( StatsType statsType, String fiatCurrency, String digCurrency, Instant timestamp )
            throws JsonParseException, JsonMappingException, IOException
    {
        String fileName = statsType.name().toLowerCase() + ".json";
        int range = 0;
        
        if( statsType == StatsType.HOURLY )
            range = 24;
        if( statsType == StatsType.DAILY )
            range = 30;
        if( statsType == StatsType.MONTHLY )
            range = 12;


        LocalDateTime ldt = LocalDateTime.ofInstant( timestamp, ZoneId.systemDefault() );
        StatsRates statsRates = new StatsRates( timestamp.getEpochSecond() );

        for( int x = 0; x <= range; x++ )
        {
            String rateValue = getHistoricalRates( fiatCurrency, digCurrency, ldt );

            if( !Strings.isNullOrEmpty( rateValue ) )
                statsRates.getRates().add( rateValue );

            if( statsType == StatsType.HOURLY )
                ldt = ldt.minusHours( 1 );
            if( statsType == StatsType.DAILY )
                ldt = ldt.minusDays( 1 );
            if( statsType == StatsType.MONTHLY )
                ldt = ldt.minusMonths( 1 );
        }

        Collections.reverse( statsRates.getRates() );

        saveFile( fiatCurrency + "/" + digCurrency, fileName, mapper.writeValueAsString( statsRates ) );

        logger.info( "Successfully created stats for: {}:{}", fiatCurrency, digCurrency );

    }

    private String getHistoricalRates( String fiatCurrency, String digCurrency, LocalDateTime ldt )
            throws JsonParseException, JsonMappingException, IOException
    {
        String rateValue = "";

        int year = ldt.getYear();
        String month = String.format( "%02d", ldt.getMonthValue() );
        String day = String.format( "%02d", ldt.getDayOfMonth() );
        String hour = String.format( "%02d", ldt.getHour() );

        String pathDay = "historical/" + digCurrency + "/" + fiatCurrency + "/" + year + "/" + month + "/" + day;
        String jsonValue = dataStoreService.getFileContent( XRATES_ROOT_FOLDER + pathDay + "/" + hour, "index.json" );

        if( !Strings.isNullOrEmpty( jsonValue ) )
        {
            TreeMap<String, String> rates = mapper.readValue( jsonValue,
                    new TypeReference<TreeMap<String, String>>() {} );
            rateValue = rates.lastEntry().getValue();
        }
        else
        {
            pathDay = "historical/" + fiatCurrency + "/" + digCurrency + "/" + year + "/" + month + "/" + day;
            jsonValue = dataStoreService.getFileContent( XRATES_ROOT_FOLDER + pathDay, "index.json" );

            if( !Strings.isNullOrEmpty( jsonValue ) )
                jsonValue.trim().replaceAll( "\"", "" );
        }

        return rateValue;
    }

    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException
    {
        dataStoreService.saveFile( true, STATS_ROOT_FOLDER + path, fileName, value );
    }

    private String getFileContent( String path, String fileName )
    {
        return dataStoreService.getFileContent( STATS_ROOT_FOLDER + path, fileName );
    }

}
