package io.hs.bex.web.config.async;


import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig implements AsyncConfigurer
{

    //@Value("${system.app.scheduler.pool.size}")
    int SCHEDULER_POOL_SIZE = 100 ;
    
    //@Value("${system.app.parser.pool.size}")
    int TASK_POOL_SIZE = 10;

    @Override
    public Executor getAsyncExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //executor.setCorePoolSize( TASK_POOL_SIZE );
        //executor.setMaxPoolSize( TASK_POOL_SIZE );
        //executor.setQueueCapacity( 250 );
        executor.setThreadNamePrefix( "BETaskExecutor" );
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
    {
        return new AsyncExceptionHandler();
    }

    @Bean( name = "BETaskSchedulerPool" )
    public ThreadPoolTaskScheduler threadPoolTaskScheduler()
    {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize( SCHEDULER_POOL_SIZE );
        threadPoolTaskScheduler.setThreadNamePrefix( "BexThreadPoolTaskScheduler" );
        return threadPoolTaskScheduler;
    }
}