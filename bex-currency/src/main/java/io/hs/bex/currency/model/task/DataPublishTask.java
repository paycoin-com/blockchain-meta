package io.hs.bex.currency.model.task;


import io.hs.bex.datastore.service.api.DataStoreService;

public class DataPublishTask implements Runnable
{
    private DataStoreService dataStoreService;
    
    public DataPublishTask( DataStoreService dataStoreService )
    {
        this.dataStoreService = dataStoreService;
    }

    @Override
    public void run()
    {
        dataStoreService.publishNS( "", "" );
    }

}
