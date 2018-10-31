package io.hs.bex.web.config;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import io.hs.bex.web.config.cache.CacheConfig;
import io.hs.bex.web.config.db.DbConfig;
import io.hs.bex.web.config.json.JacksonConfig;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "io.hs.bex.common.utils",
                                "io.hs.bex.web.config",
                                "io.hs.bex.web.controller",
                                "io.hs.bex.*.service",
                                "io.hs.bex.*.handler"})
@Import({ DbConfig.class, CacheConfig.class, JacksonConfig.class })
public class WebAppConfig implements WebMvcConfigurer
{
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    
    @Configuration
    @Profile("local")
    @PropertySource(value ={"classpath:config-local.properties"}, ignoreResourceNotFound = true)
    static class Local
    { }
    
    @Configuration
    @Profile("dev")
    @PropertySource(value ={"classpath:config-dev.properties"}, ignoreResourceNotFound = true)
    static class Test
    { }
    
    @Configuration
    @Profile("stage")
    @PropertySource("file:${gbe.config.path}/config.properties")
    static class Stage
    { }
    
    @Configuration
    @Profile("prod")
    @PropertySource("file:${bex.config.path}/config.properties")
    static class Prod
    { }
    
    @Value("${system.app.external.resource.location}")
    private String externalResource;
    
    
    // ---------- Web Settings --------------------------------
    
   
   
    @Bean
    public ViewResolver viewResolver()
    {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass( JstlView.class );
        viewResolver.setPrefix( "/WEB-INF/views/" );
        viewResolver.setSuffix( ".jsp" );
        return viewResolver;
    }

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry )
    {
        registry.addResourceHandler( "/assets/**" ).addResourceLocations( "/assets/" );
        registry.addResourceHandler( "/resources/**" ).addResourceLocations( "file:" + externalResource);
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
    

    
    // ---------- i18n Settings --------------------------------
    /*
    @Bean
    public MessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename( "classpath:i18n/messages" );
        messageSource.setDefaultEncoding( "UTF-8" );
        return messageSource;
    }*/
    
    @Bean(name="multipartResolver") 
    public CommonsMultipartResolver getResolver() throws IOException
    {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
         
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSizePerFile(224288000);//200MB
         
        return resolver;
    }
    

    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale( new Locale( "ru" ) );

        return resolver;
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) 
    {
        converters.add( stringHttpMessageConverter());
        converters.add( byteArrayHttpMessageConverter());
        converters.add( new MappingJackson2HttpMessageConverter() );
    }
    
    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() 
    {
        return new ByteArrayHttpMessageConverter();
    }
    
    
    @Override
    public void addArgumentResolvers( List<HandlerMethodArgumentResolver> argumentResolvers ) 
    {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters( false );
        resolver.setFallbackPageable(PageRequest.of( 0, 10 ));
        resolver.setMaxPageSize( 40 );
        argumentResolvers.add(resolver);
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry )
    {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName( "locale" );
        registry.addInterceptor( interceptor );
    }
    
    
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() 
    {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(Arrays.asList( new MediaType("text", "plain", UTF8),
                                                        new MediaType("text", "xml", UTF8)));
        
        return converter;
    }

}