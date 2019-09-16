package io.hs.bex.common.service;


import com.google.common.base.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class ApiAuthFilter extends AbstractAuthenticationProcessingFilter
{
    public ApiAuthFilter(final RequestMatcher requiresAuth)
    {
        super( requiresAuth );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException
    {
        String token;

        if( !Strings.isNullOrEmpty(httpServletRequest.getHeader( AUTHORIZATION )))
            token = httpServletRequest.getHeader( AUTHORIZATION );
        else if(!Strings.isNullOrEmpty( httpServletRequest.getParameter( "token" )))
            token = httpServletRequest.getParameter( "token"  );
        else
            token = "";

        Authentication requestAuthentication = new UsernamePasswordAuthenticationToken( "token", token );

        return getAuthenticationManager().authenticate( requestAuthentication );

    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final Authentication authResult) throws IOException, ServletException
    {
        SecurityContextHolder.getContext().setAuthentication( authResult );
        chain.doFilter( request, response );
    }
}
