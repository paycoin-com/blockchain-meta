package io.hs.bex.currency.task;


import io.hs.bex.datastore.service.api.DataStoreService;

public class DataPublishTask implements Runnable
{
    private DataStoreService dataStoreService;
    
    @SuppressWarnings( "unused" )
    private String keySecondary;
    
    public DataPublishTask( DataStoreService dataStoreService, String keySecondary )
    {
        this.dataStoreService = dataStoreService;
        this.keySecondary = keySecondary;
    }

    @Override
    public void run()
    {
        dataStoreService.publishNS( "", "", "" );
    }

}
