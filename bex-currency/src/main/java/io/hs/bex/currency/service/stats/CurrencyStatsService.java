package io.hs.bex.currency.service.stats;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import io.hs.bex.currency.model.CurrencyRateStack;
import io.hs.bex.currency.model.stats.CoinInfo;
import io.hs.bex.currency.model.stats.Stats;
import io.hs.bex.currency.model.stats.StatsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.stats.StatsType;
import io.hs.bex.datastore.service.api.DataStoreService;


/**
 * @author nisakov Statistics management service
 */
@Service( "CurrencyStatsService" )
public class CurrencyStatsService
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
    public void updateStatsDataAsync(List<CurrencyRateStack> rateStackList, Instant timestamp,
            List<CoinInfo> coinInfoList)
    {
        updateStatsData( rateStackList, timestamp, coinInfoList );
    }

    public void updateStatsData( List<CurrencyRateStack> rateStackList, Instant timestamp, List<CoinInfo> coinInfoList)
    {
        try
        {
            List<StatsType> statsTypeList = statsTracker.requiredUpdates( timestamp );

            for ( CurrencyRateStack rateStack : rateStackList )
            {
                for ( SysCurrency coin : rateStack.getRates().keySet() )
                {
                    CoinInfo info = coinInfoList.stream().filter( coinInfo -> coinInfo.getCoin() == coin ).findAny()
                            .orElse( new CoinInfo( coin, 0, 0 ) );

                    updateStatsData( rateStack.getCurrencyStr(), coin.getCode(),
                            rateStack.getRates().get( coin ), statsTypeList, rateStack.getTime(), info );
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
    public void createStatsDataAsync(List<CoinInfo> coinInfoList)
    {
        createStatsData( coinInfoList );
    }

    public void createStatsData(List<CoinInfo> coinInfoList)
    {
        Instant timestamp = Instant.now();

        try
        {
            for ( SysCurrency fCcy : fiatCurrencies )
            {
                for ( SysCurrency coin : digCurrencies )
                {
                    CoinInfo info = coinInfoList.stream().filter( coinInfo -> coinInfo.getCoin() == coin ).findAny()
                            .orElse( new CoinInfo( coin, 0, 0 ) );

                    saveStatsData( fCcy.getCode(), coin.getCode(), timestamp, info );
                }
            }

            logger.info( " (!!!)  **** Stats data creation ended. ***** " );
        }
        catch ( Exception e )
        {
            logger.error( "Error creating stats data:", e );
        }
    }

    public void createStatsData(String fiat, String coin, CoinInfo coinInfo)
    {
        Instant timestamp = Instant.now();

        try
        {
            saveStatsData( fiat, coin, timestamp, coinInfo );

            logger.info( " (!!!)  **** Stats data creation ended. ***** " );
        }
        catch ( Exception e )
        {
            logger.error( "Error creating stats data:", e );
        }
    }

    private void updateStatsData( String fiatCurrency, String digCurrency, String rate, List<StatsType> statsTypeList,
            Instant rateTime, CoinInfo coinInfo ) throws IOException
    {
        if(!statsTypeList.isEmpty())
        {
            Stats stats = getStats( fiatCurrency, digCurrency, rateTime );

            for ( StatsType statsType : statsTypeList )
            {
                StatsData statsData = stats.getStatsDatas().get( statsType.name() );

                if( statsData == null )
                {
                    statsData = new StatsData( rateTime.getEpochSecond(), statsType );
                    stats.getStatsDatas().put( statsType.name(), statsData );
                }

                statsData.getRates().add( rate );
                statsData.setTimestamp( rateTime.getEpochSecond() );

                //-------- Adjust size of the object --------------------
                adjustStatsDataSize( statsType, statsData );
                //-------------------------------------------------------
                if ( statsData.getStatsType() == StatsType.DAILY )
                {
                    stats.setVolume24h( coinInfo.getVolume24h() );
                    stats.setCirculatingSupply( coinInfo.getCirculatingSupply() );
                    stats.setLatestRate( Double.parseDouble( rate ) );
                }
            }
            saveFile( fiatCurrency + "/" + digCurrency, "index.json", mapper.writeValueAsString( stats ) );

            logger.info( "Successfully updated stats for: {}:{}", fiatCurrency, digCurrency );
        }
    }

    private Stats getStats( String fiatCurrency, String digCurrency, Instant timestamp ) throws IOException
    {
        String outJson = getFileContent( fiatCurrency + "/" + digCurrency, "index.json" );

        if ( !Strings.isNullOrEmpty( outJson ) )
        {
            Stats stats = mapper.readValue( outJson, Stats.class );

            if ( stats != null )
            {
                return stats;
            }
        }

        return new Stats( timestamp.getEpochSecond(), new CoinInfo( digCurrency, 0, 0 ) );
    }

    private void adjustStatsDataSize(StatsType statsType, StatsData statsRates)
    {
        if ( statsRates.getRates().size() > ( statsType.getRecordCount() + 1 ) )
            statsRates.getRates().remove( 0 );
    }

    private void saveStatsData(String fiatCurrency, String digCurrency, Instant timestamp, CoinInfo coinInfo)
            throws IOException
    {
        Stats stats = new Stats( timestamp.getEpochSecond(), coinInfo );

        for ( StatsData statsData : stats.getStatsDatas().values() )
        {
            LocalDateTime ldt = LocalDateTime.ofInstant( timestamp, ZoneId.systemDefault() );

            for ( int x = 0; x <= statsData.getStatsType().getRecordCount(); x++ )
            {
                String rateValue = getHistoricalXRates( fiatCurrency, digCurrency, ldt );

                if ( !Strings.isNullOrEmpty( rateValue ) )
                    statsData.getRates().add( rateValue );

                ldt = adjustDateTime( statsData.getStatsType(), ldt );
            }

            if ( statsData.getRates().size() > 0 )
            {
                if ( statsData.getStatsType() == StatsType.DAILY )
                    stats.setLatestRate( Double.parseDouble( statsData.getRates().get( 0 ) ) );

                Collections.reverse( statsData.getRates() );
            }
        }

        saveFile( fiatCurrency + "/" + digCurrency, "index.json", mapper.writeValueAsString( stats ) );

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
