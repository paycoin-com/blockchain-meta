package io.hs.bex.web.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableCaching
public class CacheConfig
{
    /*
     * Disable Simple Caching
     * 
     * @Bean public CacheManager cacheManager() { SimpleCacheManager
     * cacheManager = new SimpleCacheManager(); cacheManager.setCaches(
     * Arrays.asList( new ConcurrentMapCache( "iplocations" ) ) ); return
     * cacheManager; }
     */
    
    
    @Bean
    public CacheManager cacheManager()
    {
        return new EhCacheCacheManager( ehCacheCacheManager().getObject() );
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager()
    {
        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setConfigLocation( new ClassPathResource( "ehcache.xml" ) );
        factory.setShared( true );
        return factory;
    }

}