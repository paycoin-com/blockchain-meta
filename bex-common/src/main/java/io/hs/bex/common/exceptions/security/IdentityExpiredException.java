package io.hs.bex.common.exceptions.security;

public class IdentityExpiredException extends SystemSecurityException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IdentityExpiredException()
    {
        super();
    }


    public IdentityExpiredException( final String message )
    {
        super( message );
    }


    public IdentityExpiredException( final String message, final Throwable cause )
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
