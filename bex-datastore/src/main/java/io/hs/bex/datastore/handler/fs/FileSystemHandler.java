package io.hs.bex.datastore.handler.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.hs.bex.datastore.model.FileInfo;
import io.hs.bex.datastore.service.api.DataStoreHandler;


@Service( "FileSystemHandler" )
public class FileSystemHandler implements DataStoreHandler
{
    // ---------------------------------
    // private static final Logger logger = LoggerFactory.getLogger(
    // FileSystemHandler.class );
    // ---------------------------------

    //final private String SUB_DATA_PATH = "/blockstore";

    @Value( "${system.app.external.resource.location}" )
    private String root_dir;
    
    public FileSystemHandler() {}

    public FileSystemHandler( String root_dir ) 
    {
        this.root_dir = root_dir;
    }
    
    
    @Override
    public String createDir( String pathStr, boolean createParent ) throws IOException
    {
        Path path  = Paths.get( root_dir + pathStr );
        Files.createDirectories( path );

        return path.toString();
    }

    @Override
    public String getFileContent( String pathStr, String fileName ) throws IOException
    {
        try 
        {
            String file = root_dir + pathStr + File.separator + fileName;
            StringBuilder sb = new StringBuilder();
          
            FileInputStream inputStream = new FileInputStream( file );

            int content = 0;
            
            while ((content = inputStream.read()) != -1) 
            {
                sb.append( (char) content);
            }

            inputStream.close();  
            
            return sb.toString();
            
        }
        catch( FileNotFoundException e ) 
        {
            return "";
        }
    }

    @Override
    public String saveFile( boolean createDir, String pathStr, String fileName, String jsonData ) throws IOException
    {
        if( createDir )
            createDir( pathStr, true );

        Path path = Paths.get( root_dir + pathStr + File.separator +"/"+ fileName );
        
        Files.write( path , jsonData.getBytes() );
        
        return fileName;
    }

    @Override
    public FileInfo getFileInfo( String path, String fileName )
    {
        return null;
    }

    @Override
    public String publishNS( String nsValue )
    {
        return null;
    }

    @Override
    public String publishNS( String path, String fileName )
    {
        return null;
    }

}
