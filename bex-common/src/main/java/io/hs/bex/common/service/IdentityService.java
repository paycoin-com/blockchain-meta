package io.hs.bex.common.service;


import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import io.hs.bex.common.dao.UserDAO;
import io.hs.bex.common.exceptions.dao.DaoException;
import io.hs.bex.common.exceptions.security.InvalidAuthException;
import io.hs.bex.common.model.CustomUserDetails;
import io.hs.bex.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Strings;


@Service("identityService")
@Transactional( readOnly = true )
public class IdentityService
{
    //---------------------------------
    private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);
    //---------------------------------

    @Autowired
    UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ******************************************
     * init
     */
    @PostConstruct
    public void init()
    {
        //insert default User
        if(getUserList().isEmpty())
        {
            User user =  new User( "admin","secret", "");
            saveUser( user );
        }
    }


    /**************************************************
     *
     */
    
    public User auhtenticateUser( String userName, String password ) throws InvalidAuthException
    {
        User user;

        try
        {
            user = userDAO.findActiveByUserName( userName );

            if ( user != null )
            {
                if (!passwordEncoder.matches(password, user.getPassword()))
                    throw new InvalidAuthException("");// invalid credentials exception
            }
            else
                throw new InvalidAuthException();
        }
        catch ( DaoException e )
        {
            throw new InvalidAuthException();
        }
        return user;
    }


    /**************************************************
     *
     */
    
    public User auhtenticateByToken( String token ) throws InvalidAuthException
    {
        User user;

        try
        {
            user = userDAO.findActiveByToken( token );

            if ( user != null )
                return user;
            else
                throw new InvalidAuthException();
        }
        catch ( DaoException e )
        {
            throw new InvalidAuthException();
        }
    }


    /*  ************************************************
     *
     */
    public CustomUserDetails loginUser( String userName , String password  ) throws InvalidAuthException
    {
        CustomUserDetails userDetails = null;
        User user = null;

        if("token".equals( userName ))
            user = auhtenticateByToken( password );
        else
            user = auhtenticateUser( userName, password );

        if(user != null)
        {
            userDetails = new CustomUserDetails( user );
            //user.setLastLogin( new Date(System.currentTimeMillis()) );
            //userDAO.save( user );

            return userDetails;
        }
        else
            return null;
    }


    /* *************************************
     * Get All Registered Users
     */
    public List<User> getUserList()
    {
        try
        {
            return userDAO.findAll();
        }
        catch(Exception e)
        {
            logger.error( " **** Error getting user list:" , e );
            return Collections.emptyList();
        }
    }


    /* *************************************
     * Get Detailed users info
     */
    public User getUserDetailsById( long userId )
    {
        try
        {
            return userDAO.findDetailsById( userId );
        }
        catch(Exception e)
        {
            logger.info( " **** User details not found for userid:" + userId , e );
            return null;
        }
    }


    /* *************************************
     * Register new Users
     */
    @Transactional
    public User saveUser( User user )
    {
        try
        {
            isValidUserName( user.getUserName() );
            isValidPassword( user.getUserName(), user.getPassword() );

            user.setPassword( passwordEncoder.encode( user.getPassword() ) );

            return userDAO.save( user );
        }
        catch(Exception e)
        {
            logger.error( " **** Error registering new user:" , e );
            return null;
        }
    }


    /* *************************************************
     *
     */
    private void isValidUserName( String userName )
    {
        if ( Strings.isNullOrEmpty( userName ) || userName.length() < 4 )
        {
            throw new IllegalArgumentException( "User name cannot be shorter than 4 characters." );
        }

        if ( userName.equalsIgnoreCase( "token" ) || userName.equalsIgnoreCase( "administrator" )
                || userName.equalsIgnoreCase( "system" ) )
        {
            throw new IllegalArgumentException( "User name is reserved by the system." );
        }
    }


    /* *************************************************
     *
     */
    private void isValidPassword( String userName, String password )
    {
        if ( Strings.isNullOrEmpty( password ) || password.length() < 4 )
        {
            throw new IllegalArgumentException( "Password cannot be shorter than 4 characters" );
        }

        if ( password.equalsIgnoreCase( userName ) || password.equalsIgnoreCase( "password" )
                || password.equalsIgnoreCase( "system" ) )
        {
            throw new IllegalArgumentException( "Password doesn't match security requirements" );
        }
    }
}