package io.hs.bex.datastore.service;


import io.hs.bex.datastore.model.DataStoreType;
import io.hs.bex.datastore.model.FileInfo;
import io.hs.bex.datastore.service.api.DataStoreHandler;
import io.hs.bex.datastore.service.api.DataStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service( "DataStoreService" )
public class DataStoreServiceImpl implements DataStoreService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( DataStoreServiceImpl.class );
    // ---------------------------------
    
    @Autowired
    ApplicationContext context;
    
    @Autowired
    @Qualifier("IpfsHandler")
    DataStoreHandler defaultDataStoreHandler;
    
    @Async
    @Override
    public String createDirAsync( String path, boolean createParent ) 
    {
        return createDir( path, createParent );
    }
    
    @Override
    public String createDir( String path, boolean createParent ) 
    {
        try 
        {
            return defaultDataStoreHandler.createDir( path, createParent );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error creating dir" , e );
            
            return null;
        }
    }
    
    @Override
    public String createDir( DataStoreType dataStoreType, String path, boolean createParent ) 
    {
        try 
        {
            return ((DataStoreHandler)context.getBean( dataStoreType.getHandlerName() ))
                    .createDir( path, createParent );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error creating dir" , e );
            
            return null;
        }
    }
    
    @Async
    @Override
    public String saveFileAsync( boolean createDir, String path, String fileName, String jsonData ) 
    {
        return saveFile( createDir, path, fileName, jsonData );
    }
    

    @Override
    public String saveFile( boolean createDir, String path, String fileName, String jsonData ) 
    {
        try 
        {
            return defaultDataStoreHandler.saveFile( createDir, path, fileName, jsonData );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error creating file" , e );
            
            return null;
        }
    }
    
    @Override
    public String saveFile( DataStoreType dataStoreType, boolean createDir, String path, String fileName, String jsonData ) 
    {
        try 
        {
            return ((DataStoreHandler)context.getBean( dataStoreType.getHandlerName() ))
                    .saveFile( createDir, path, fileName, jsonData );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error creating file" , e );
            
            return null;
        }
    }
    
    @Override
    public String getFileContent( String path, String fileName ) 
    {
        try 
        {
            return defaultDataStoreHandler.getFileContent( path, fileName );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error fetching file" , e );
            
            return "";
        }
    }
    
    @Override
    public String getFileContent( DataStoreType dataStoreType, String path, String fileName ) 
    {
        try 
        {
            return ((DataStoreHandler)context.getBean( dataStoreType.getHandlerName() ))
                    .getFileContent( path, fileName );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error fetching file" , e );
            
            return "";
        }
    }
    
    
    @Override
    public FileInfo getFileInfo( String path, String fileName ) 
    {
        try 
        {
            return defaultDataStoreHandler.getFileInfo( path, fileName );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error fetching file info " , e );
            
            return null;
        }
    }
    
    @Override
    public FileInfo getFileInfo( DataStoreType dataStoreType, String path, String fileName ) 
    {
        try 
        {
            return ((DataStoreHandler)context.getBean( dataStoreType.getHandlerName() ))
                    .getFileInfo( path, fileName );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error fetching file info " , e );
            
            return null;
        }
    }
    
    
    @Override
    public String publishNS( String nsValue) 
    {
        try 
        {
            return defaultDataStoreHandler.publishNS( nsValue );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error publishing namespace {} " ,nsValue, e );
            
            return null;
        }
    }


    @Override
    public String publishNS( String path, String fileName ) 
    {
        try 
        {
            return defaultDataStoreHandler.publishNS( path, fileName );
        }
        catch( Exception e ) 
        {
            logger.error( "(E!) Error publishing namespace path:{} " ,path, e );
            
            return null;
        }
    }
    
   
    
    @Async
    @Override
    public String publishNSAsync( String path, String fileName ) 
    {
        return publishNS( path, fileName ) ;
    }


}
