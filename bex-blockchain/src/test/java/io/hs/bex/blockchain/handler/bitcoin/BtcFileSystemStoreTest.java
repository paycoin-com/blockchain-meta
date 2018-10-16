package io.hs.bex.blockchain.handler.bitcoin;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.model.store.AddressData;
import io.hs.bex.datastore.handler.fs.FileSystemHandler;

public class BtcFileSystemStoreTest
{

    private FileSystemHandler fsHandler = new FileSystemHandler(); 
    
    @Before
    public void setUp() throws Exception
    {}

    @Test
    public void test() throws JsonProcessingException, IOException
    {
        String path = "/tmp/blocks";
        String fileName = "index.json";
        
        ObjectMapper mapper = new ObjectMapper();
        
        AddressData addressData1 = new AddressData();
        addressData1.setAddress( "ADDRESS_1" );
        addressData1.addBlockData( "HASH_OF_THE_BLOCK_1", 10001 );
        addressData1.addBlockData( "HASH_OF_THE_BLOCK_2", 10002 );
        
        AddressData addressData2 = new AddressData();
        addressData2.addBlockData( "HASH_OF_THE_BLOCK_1", 10002 );
        addressData2.addBlockData( "HASH_OF_THE_BLOCK_4", 10003 );

        fsHandler.saveFile( true, path, fileName, mapper.writeValueAsString( addressData1 ) );
        
        String payload = fsHandler.getFileContent( path, fileName);
        
        addressData1 = mapper.readValue( payload, AddressData.class );
        addressData1.addBlocks( addressData2.getBlocks() );
        fsHandler.saveFile( true, path, fileName, mapper.writeValueAsString( addressData1 ) );
        
        System.out.println( "Final Content:" + fsHandler.getFileContent( path, fileName));
    }

}
