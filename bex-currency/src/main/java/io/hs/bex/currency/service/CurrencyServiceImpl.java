package io.hs.bex.currency.service;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
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
import io.hs.bex.currency.handler.CryptoCompareInfoHandler;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.task.HourlyXRatesTask;
import io.hs.bex.currency.task.LatestXRatesTask;
import io.hs.bex.currency.utils.CurrencyUtils;
import io.hs.bex.currency.task.DataPublishTask;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import io.hs.bex.currency.service.api.CurrencyService;
import io.hs.bex.datastore.service.api.DataStoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;


@Service( "CurrencyService" )
public class CurrencyServiceImpl implements CurrencyService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CryptoCompareInfoHandler.class );
    // ---------------------------------
    
    final int LAST_XRATES_FETCH_PERIOD = 180; //seconds
    final int HOURLY_XRATES_FETCH_PERIOD = 1800; //seconds
    
    final String XRATES_ROOT_FOLDER = "/xrates";
    
    @Autowired
    ObjectMapper mapper;
    
    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    CurrencyTaskManager taskManager;
    
    @Autowired
    DataStoreService dataStoreService;

    CurrencyInfoService defaultInfoService;
    
    private CurrencyInfoRequest currencyTaskParams = new CurrencyInfoRequest();

    @PostConstruct
    public void init() 
    {
        defaultInfoService = (CurrencyInfoService) appContext.getBean( "CryptoCompareInfoHandler" );
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
    
    private DataPublishTask startDataPublishTask() 
    {
        return new DataPublishTask( dataStoreService ); 
    }

    public void buildTaskParams() 
    {
        currencyTaskParams.clearCurrencies();
        
        currencyTaskParams.getSourceCurrencies().addAll( getSupported( CurrencyType.DIGITAL ));
        currencyTaskParams.getTargetCurrencies().addAll( getSupported( CurrencyType.FIAT ));
    }
    
    @Override
    public void startSyncJob()
    {
        taskManager.startScheduledAtFixed( startHourlyXRatesTask(), "HourlyXRatesTask", 0, HOURLY_XRATES_FETCH_PERIOD );
        taskManager.startScheduledAtFixed( startLatesXRatesTask(), "LatesXRatesTask", 0, LAST_XRATES_FETCH_PERIOD );
        taskManager.startScheduledTask( startDataPublishTask(), "DataPublishProcessTask", 60, 60 );
    }

    @Override
    public CurrencyInfoService getInfoService()
    {
        return defaultInfoService;
    }
    
    @Override
    public Set<SysCurrency> getCurrencyList()
    {
        try
        {
            HashSet<SysCurrency> currencies = new HashSet<>(Arrays.asList(SysCurrency.values()));
            
            String data  = getFileContent( "", "index.json" );
            
            if(!Strings.isNullOrEmpty( data )) 
            {
                List<SysCurrency> supported = mapper.readValue(data, new TypeReference<List<SysCurrency>>(){});
                currencies.addAll( supported );
            }
            
            return currencies; 
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency list" , e );
        }
        
        return Collections.emptySet();
    }
    
    @Override
    public SysCurrency getCurrencyDetails( String code )
    {
        try
        {
            SysCurrency currency = SysCurrency.find( code );
            String details = getFileContent( "/" + currency.getCode(), "index.json");
            currency.setDetails( details );
            
            return currency; 
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency details" , e );
        }
        
        return SysCurrency.OTHER;
    }
    
    
    @Override
    public List<SysCurrency> getSupported( CurrencyType currencyType )
    {
        try
        {
            String data  = getFileContent( "", "index.json" );
            List<SysCurrency> supported = mapper.readValue(data, new TypeReference<List<SysCurrency>>(){});
            
            if( CurrencyType.OTHER  != currencyType ) 
            {
                return supported.stream().filter( c -> c.getType() == currencyType ).collect(Collectors.toList());
            }
            
            return supported;
        }
        catch( Exception e )
        {
            logger.error( "Error getting currency list (supported:)" , e );
        }
        
        return Collections.emptyList();
    }
    
    
    @Override
    public void setSupported(String[] supported)
    {
        try
        {
            HashSet<SysCurrency> currencies = new HashSet<>();
            
            for( String code: supported ) 
            {
                currencies.add( SysCurrency.find( code));
            }
            
            saveFile( "", "index.json", mapper.writeValueAsString( currencies ) );
            
            //-----------------------
            buildTaskParams();
            //-----------------------
        }
        catch( Exception e )
        {
            logger.error( "Error setting supporeted currences" , e );
        }

    }
    
    @Override
    public void updateCurrency(String code, String details)
    {
        try
        {
            SysCurrency currency = SysCurrency.find( code );
            saveFile( "/" + currency.getCode(), "index.json", details );
        }
        catch( Exception e )
        {
            logger.error( "Error updating  currencies" , e );
        }
    }


    @Override
    public void saveXRates( CurrencyInfoRequest request ) 
    {
        try 
        {
            String path = "";
            
            for( SysCurrency sourceCurrency : request.getSourceCurrencies() ) 
            {
                for(SysCurrency targetCurrency : request.getTargetCurrencies() ) 
                {
                    Map<String,Float> dataMap = new LinkedHashMap<>();
                    LocalDateTime lastDate = null, localDateTime = null;
                    
                    int hour = 0;
                    
                    String rootPath =  "/" + sourceCurrency.getCode() + "/" + targetCurrency.getCode() +"/";
                    List<CurrencyRate> xrates = defaultInfoService.getXRatesBy( 
                            new CurrencyInfoRequest( sourceCurrency, targetCurrency, 
                                    request.getPeriod(), request.getDateTo(), request.getLimit()) ) ;
                    
                    for( CurrencyRate xrate: xrates ) 
                    {
                        localDateTime = xrate.getLocalDateTime();
                        
                        if( request.getPeriod() == TimePeriod.MINUTE ) 
                        {
                            if( hour != localDateTime.getHour()) 
                            {
                                if(dataMap.size() > 0) 
                                {
                                    path = CurrencyUtils.buildDirStructure( TimePeriod.HOUR, rootPath,lastDate );
                                    
                                    appendData( path, "index.json", dataMap );
                                    dataMap.clear();
                                }
                            }
                            
                            dataMap.put( String.format( "%02d", localDateTime.getMinute()), xrate.getRate() );
                        }
                        else 
                        {
                            path = CurrencyUtils.buildDirStructure( request.getPeriod(), rootPath, localDateTime );
                            saveFile( path, "index.json", Float.toString( xrate.getRate() ));
                        }
                      
                        hour = localDateTime.getHour();
                        lastDate = localDateTime;
                    }
                    
                    if( dataMap.size() > 0 ) 
                    {
                        path = CurrencyUtils.buildDirStructure( TimePeriod.HOUR, rootPath, lastDate );
                        appendData( path, "index.json", dataMap );
                    }
                }
            
                //------------------------------------------------------------------------
                logger.info( "Successfully fetchAndStore XRates for:{}-{}", sourceCurrency.getCode(), 
                        StringUtils.instantToString( request.getDateTo() ) );
                //------------------------------------------------------------------------
            }
        }
        catch( Exception e ) 
        {
            logger.error( "Error saving xrates for:{}", request, e );
        }
    }
    
    private void appendData( String path, String fileName,  Map<String,Float> dataMap ) throws IOException 
    {
        LinkedHashMap<String,Float> contentMap = null;
        
        String content = getFileContent( path, fileName );
        
        if(!Strings.isNullOrEmpty( content )) 
        {
            contentMap = mapper.readValue(content, new TypeReference<LinkedHashMap<String,Float>>(){});
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
            List<CurrencyRate> xrates = defaultInfoService.getLatestXRates( request ) ;
            
            for( CurrencyRate xrate: xrates ) 
            {
                String rootPath =  "/" + xrate.getCurrency().getCode() 
                        + "/" + xrate.getTargetCurrency().getCode() +"/";
                saveFile( rootPath, "index.json", mapper.writeValueAsString( xrate ));
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
        request.getSourceCurrencies().addAll( currencyTaskParams.getSourceCurrencies() );
        request.getTargetCurrencies().addAll( currencyTaskParams.getTargetCurrencies() );
        request.setPeriod( timePeriod );
        request.setDateTo( Instant.now() );
        request.setLimit( fetchSize );
        
        if(storeType == 1) 
            saveXRates( request );
        else 
            saveLatestXRates( request );
    }
    
    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException 
    {
        dataStoreService.saveFile( true , XRATES_ROOT_FOLDER + path, "index.json", value );
    }

    
    private String getFileContent( String path, String fileName )
    {
        return dataStoreService.getFileContent( XRATES_ROOT_FOLDER + path , "index.json");
    }
   
}
