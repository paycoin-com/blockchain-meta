package io.hs.bex.web.config;


import io.hs.bex.common.service.ApiAuthFilter;
import io.hs.bex.common.service.CustomAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Spring Security Configuration
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig
{
    private static final RequestMatcher PROTECTED_API_URL = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/**")
    );

    @Autowired
    public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.authenticationProvider(customAuthProvider());
    }

    @Bean
    public CustomAuthProvider customAuthProvider()
    {
        return new CustomAuthProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        protected void configure(HttpSecurity http) throws Exception
        {
            http
                    .antMatcher( "/api/**" )
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling().authenticationEntryPoint( new HttpStatusEntryPoint(HttpStatus.FORBIDDEN) )
                    .and()
                    //.authenticationProvider(new CustomAuthProvider())
                    .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter.class)
                    //.addFilterBefore(new ExceptionTranslationFilter( new Http403ForbiddenEntryPoint()),
                            //GenericFilterBean.class
                    //)
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .logout().disable();
        }

        @Bean
        ApiAuthFilter authenticationFilter() throws Exception
        {
            final ApiAuthFilter filter = new ApiAuthFilter(PROTECTED_API_URL);
            filter.setAuthenticationManager(authenticationManager());
            //filter.setAuthenticationSuccessHandler(successHandler());
            return filter;
        }

        @Bean
        AuthenticationEntryPoint forbiddenEntryPoint()
        {
            return new HttpStatusEntryPoint( HttpStatus.FORBIDDEN);
        }
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        public void configure(WebSecurity web) throws Exception
        {
            web.ignoring().antMatchers("/assets/**","/resources/**");
        }

        @Override
        protected void configure( HttpSecurity http ) throws Exception
        {
            http
                    // ****** Main Access Config ********

                    .authorizeRequests()
                    .antMatchers("/locale*","/login*","/auth*" ).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    // ****** Auth/Login Config ********

                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl( "/auth" )
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/")
                    .failureUrl("/login?error")
                    .permitAll()
                    .and()
                    // ****** Logout Config ************

                    .logout()
                    .logoutUrl( "/logout" )
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .and()
                    // ****** Error Config **************

                    .exceptionHandling()
                    .accessDeniedPage("/access_denied")
                    .and()
                    // ****** Session Config *************

                    .sessionManagement()
                    .maximumSessions(1)
                    .expiredUrl("/login?expired");
        }
    }
}