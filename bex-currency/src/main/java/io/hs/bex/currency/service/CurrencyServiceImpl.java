package io.hs.bex.currency.service;


import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import io.hs.bex.currency.handler.CryptoCompareInfoHandler;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.CurrencyType;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.model.task.CurrencyDataTask;
import io.hs.bex.currency.model.task.DataPublishTask;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import io.hs.bex.currency.service.api.CurrencyService;
import io.hs.bex.currency.utils.CurrencyUtils;
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

    @PostConstruct
    public void init() 
    {
        defaultInfoService = (CurrencyInfoService) appContext.getBean( "CryptoCompareInfoHandler" );
    }
    
    private CurrencyDataTask startCurrencyTask() 
    {
        return new CurrencyDataTask(this, buildTaskParams() ); 
    }
    
    private DataPublishTask startDataPublishTask() 
    {
        return new DataPublishTask( dataStoreService ); 
    }

    
    private List<CurrencyInfoRequest> buildTaskParams() 
    {
        List<CurrencyInfoRequest> requests = new ArrayList<>();
        List<SysCurrency> digitalCurrencies = getSupported( CurrencyType.DIGITAL );
        List<SysCurrency> fiatCurrencies = getSupported( CurrencyType.FIAT );
        
        for( SysCurrency digCurrency: digitalCurrencies ) 
        {
            for( SysCurrency fiatCurrency: fiatCurrencies ) 
            {
                CurrencyInfoRequest req = new CurrencyInfoRequest( digCurrency, fiatCurrency, TimePeriod.MINUTE, null, 0);
                requests.add( req );
            }
        }
        
        return requests;
    }
    
    @Override
    public void startSyncJob()
    {
        taskManager.startScheduledAtFixed( startCurrencyTask(), "CurrencyDataProcessTask", 0, 180 );
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
    public void saveCurrencyRates( CurrencyInfoRequest request , boolean storeLastRate ) 
    {
        try 
        {
            float lastRate = 0;
            List<CurrencyRate> xrates = defaultInfoService.getCurrencyRateBy( request ) ;
            String rootPath =  "/" + request.getSourceCurrency().getCode() + "/" + request.getTargetCurrency().getCode() +"/";
            
            for( CurrencyRate xrate: xrates ) 
            {
                lastRate = xrate.getRate();

                
            }
            
            //saveFile( CurrencyUtils.buildDirStructure( request.getPeriod(), rootPath, xrate.getDate()), "index.json", lastRate );

            
            logger.info( "(!) Successfully fetched and saved currency S:{}-T:{} Date:{}",
                    request.getSourceCurrency().getCode(),request.getTargetCurrency().getCode(), 
                    request.getDateTo().atZone(ZoneId.systemDefault()).toLocalDateTime() );

        }
        catch( Exception e ) 
        {
            logger.error( "Error saving xrates for:{}", request, e );
        }
    }
    
//    private void saveFile( String path, String fileName, float value ) throws JsonProcessingException 
//    {
//        dataStoreService.saveFile( true , XRATES_ROOT_FOLDER + path, "index.json", Float.toString( value ) );
//    }
    
    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException 
    {
        dataStoreService.saveFile( true , XRATES_ROOT_FOLDER + path, "index.json", value );
    }

    
    private String getFileContent( String path, String fileName )
    {
        return dataStoreService.getFileContent( XRATES_ROOT_FOLDER + path , "index.json");
    }
   
}
