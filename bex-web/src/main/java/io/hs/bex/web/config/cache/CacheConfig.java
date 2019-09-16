package io.hs.bex.web.config.cache;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;


@Configurable
@EnableCaching
public class CacheConfig
{
    @Bean
    public CacheManager getEhCacheManager()
    {
        return new EhCacheCacheManager( getEhCacheFactory().getObject() );
    }

    @Bean
    public EhCacheManagerFactoryBean getEhCacheFactory()
    {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation( new ClassPathResource( "ehcache.xml" ) );
        factoryBean.setShared( true );
        return factoryBean;
    }
}