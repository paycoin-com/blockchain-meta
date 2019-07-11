package io.hs.bex.common.exceptions.security;

import javax.security.sasl.AuthenticationException;

public class InvalidAuthException extends AuthenticationException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public InvalidAuthException()
    {
        super();
    }


    public InvalidAuthException( final String message )
    {
        super( message );
    }


    public InvalidAuthException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
    

    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public StackTraceElement[] getStackTrace()
    {
        return super.getStackTrace();
    }
        

}
