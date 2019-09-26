package io.hs.bex.common.service;


import com.google.common.base.Strings;
import io.hs.bex.common.exceptions.security.InvalidAuthException;
import io.hs.bex.common.model.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthProvider implements AuthenticationProvider
{
    //---------------------------------
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthProvider.class);
    //---------------------------------

    @Autowired
    IdentityService identityService;


    /* ********************************************
     *
     * */
    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException
    {
        String userName = authentication.getName().trim();
        String password = authentication.getCredentials().toString().trim();

        try
        {
            if( Strings.isNullOrEmpty(userName))
                userName = "token";

            CustomUserDetails userDetails = identityService.loginUser( userName, password);

            if(userDetails != null)
            {
                return new UsernamePasswordAuthenticationToken( userDetails, password, userDetails.getUser().getAuthorities() );
            }
            else
            {
                throw new InvalidAuthException();
            }
        }
        catch(Exception e)
        {
            logger.error( "Authentication Failed for User:" + userName );

            return null;
        }
    }


    /* ********************************************
     *
     * */
    @Override
    public boolean supports( Class<?> authentication )
    {
        return authentication.equals( UsernamePasswordAuthenticationToken.class );
    }



}
