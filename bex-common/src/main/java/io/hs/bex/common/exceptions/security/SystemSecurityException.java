package io.hs.bex.common.exceptions.security;

/**
 *  General/Overall System Security Exception
 */
public class SystemSecurityException extends SecurityException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public SystemSecurityException()
    {
        super();
    }


    public SystemSecurityException( final String message )
    {
        super( message );
    }


    public SystemSecurityException( final String message, final Throwable cause )
    {
        super( message, cause );
    }


    public SystemSecurityException( final Throwable cause )
    {
        super( cause );
    }


    @Override
    public String toString()
    {
        return super.toString();
    }

}