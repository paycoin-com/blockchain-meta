package io.hs.bex.blockchain.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;


@Service("BlockChainTaskManager")
public class BlockChainTaskManager
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainTaskManager.class );
    // ---------------------------------
    
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @PostConstruct
    public void init() 
    {
        
    }
    
    public void startScheduledTask( Runnable runnableTask, String taskName, int startAfter, int interval ) 
    {
        taskScheduler.scheduleWithFixedDelay( runnableTask, 
                (Instant.now()).plus( startAfter, ChronoUnit.SECONDS),
                Duration.ofSeconds( interval) );
        
        logger.info( "(!) Started new task:" + taskName );
    }
    
    
    public void startScheduledAtFixed( Runnable runnableTask, String taskName, int startAfter, int period ) 
    {
        taskScheduler.scheduleAtFixedRate(  runnableTask, 
                (Instant.now()).plus( startAfter, ChronoUnit.SECONDS),
                Duration.ofSeconds( period) );
        
        logger.info( "(!) Started new task:" + taskName );
    }

}
