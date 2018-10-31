package io.hs.bex.web.config;


import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


@Configuration
public class WebAppInit extends AbstractAnnotationConfigDispatcherServletInitializer
{
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class[] { WebAppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return null;
    }

    @Override
    protected String[] getServletMappings()
    {
        return new String[] { "/" };
    }

    @Override
    public void onStartup( ServletContext servletContext ) throws ServletException
    {
        super.onStartup( servletContext );
        
        //--------- Handle 404 errors ---------------
        ServletRegistration.Dynamic servlet = (Dynamic) servletContext.getServletRegistration( "dispatcher" );
        servlet.setInitParameter("throwExceptionIfNoHandlerFound", "true");
        //-------------------------------------------


        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter( "encoding-filter",
                        new CharacterEncodingFilter() );
        encodingFilter.setInitParameter( "encoding", "UTF-8" );
        encodingFilter.setInitParameter( "forceEncoding", "true" );
        encodingFilter.addMappingForUrlPatterns( null, true, "/*" );
        servletContext.addListener(new RequestContextListener());
        
        if(System.getProperty("spring.profiles.active") == null)
            System.setProperty("spring.profiles.active", "dev");
        
        if(System.getProperty("bex.config.path") == null)
            System.setProperty("bex.config.path", "/opt/bex/conf");
    }
}