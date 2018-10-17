package io.hs.bex.datastore.handler.fs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.hs.bex.datastore.service.api.DataStoreHandler;

public class FileSystemHandlerTest
{
    private final String ROOT_DIR = "/tmp"; 
    
    DataStoreHandler storeHandler;
    
    @Before
    public void setUp() throws Exception
    {
        storeHandler = new FileSystemHandler( ROOT_DIR );
    }

    @Ignore
    @Test
    public void testCreateDir() throws IOException
    {
        String test_dir = "/x1/x2/x3";
        
        String pathStr = storeHandler.createDir( test_dir, true );
        
        assertEquals( pathStr, ROOT_DIR + "/blockstore" + test_dir );
    }
    
    
    //@Ignore
    @Test
    public void testWriteReadFile() throws IOException
    {
        String test_dir = "/x1/x2/x3/x4";
        String fileName = "test.json";
        String content = "{json:data1,data2}";
        
        storeHandler.saveFile( true, test_dir, fileName, content );
        
        System.out.println( "FileContent:" + storeHandler.getFileContent( test_dir, fileName ) );
          
        assertTrue(Files.exists( Paths.get( ROOT_DIR + "/blockstore" + test_dir + File.separator + fileName )));
    }


}
