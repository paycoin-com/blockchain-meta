package io.hs.bex.common.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public class CustomUserDetails  implements UserDetails
{

    /**
     *
     */
    private static final long serialVersionUID = 5308263650386162916L;

    private User user;

    private String name;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    //******************************************
    public CustomUserDetails(){}

    public CustomUserDetails( User user )
    {
        this.user = user;
    }

    public String getUserId()
    {
        return Long.toString( user.getId());
    }

    @Override
    public String getPassword()
    {
        return user.getPassword();
    }

    @Override
    public String getUsername()
    {
        return user.getUserName();
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<GrantedAuthority> getAuthorities()
    {
        return user.getAuthorities();
    }

    public void setAuthorities( List<GrantedAuthority> authorities )
    {
        user.getAuthorities().clear();
        user.getAuthorities().addAll( authorities );
    }

    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }

    public void setAccountNonExpired( boolean accountNonExpired )
    {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }

    public void setAccountNonLocked( boolean accountNonLocked )
    {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired()
    {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired( boolean credentialsNonExpired )
    {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled()
    {
        return user.getStatus() == 1 ? true : false;
    }

    public void setEnabled( boolean enabled )
    {
        user.setStatus( (enabled ? 1:2) );
    }

}
