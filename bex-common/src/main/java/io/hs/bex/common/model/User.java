package io.hs.bex.common.model;


import com.google.common.base.Strings;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity @Table( name = "tb_user" ) public class User implements Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "user_name", length = 80, nullable = false, unique = true )
    private String userName;

    @Column( name = "password", length = 100, nullable = false )
    private String password;

    @Column( name = "type" )
    private short type = 2;

    @Column( name = "status" )
    private short status = 1;

    @Column( name = "token", length = 120 )
    private String token;

    // *********************************************
    public User()
    {
    }

    public User(String userName, String password, String token)
    {
        this.userName = userName;
        this.password = password;

        if ( Strings.isNullOrEmpty( token ) )
            this.token = UUID.randomUUID().toString();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public short getType()
    {
        return type;
    }

    public void setType(short type)
    {
        this.type = type;
    }

    public short getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = (short) status;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public List<GrantedAuthority> getAuthorities()
    {
        List<GrantedAuthority> roleList = new ArrayList<>();

        roleList.add( new SimpleGrantedAuthority( "admin" ) );

        return roleList;
    }
}