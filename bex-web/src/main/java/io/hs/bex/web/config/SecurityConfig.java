package io.hs.bex.web.config;


import io.hs.bex.common.service.CustomAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Spring Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    @Autowired
    public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.authenticationProvider(customAuthProvider());
    }

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
                .antMatchers("/locale*","/login*","/auth*","/api/**").permitAll()
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


    @Bean
    public AuthenticationProvider customAuthProvider()
    {
        return  new CustomAuthProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}