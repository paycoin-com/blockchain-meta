package io.hs.bex.common.dao;


import io.hs.bex.common.exceptions.dao.DaoException;
import io.hs.bex.common.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserDAO extends JpaRepository<User, Long>
{
    //********************************************
    @Query( value = "SELECT u "
            + " FROM User u "
            + " WHERE LOWER(u.userName) LIKE LOWER(:userName) "
            + " AND u.status = 1 " )
    User findActiveByUserName(@Param( "userName" ) String userName) throws DaoException;

    //********************************************
    @Cacheable("users_cache")
    @Query( value = "SELECT u "
            + " FROM User u "
            + " WHERE LOWER(u.token) LIKE LOWER(:token) "
            + " AND u.status = 1 " )
    User findActiveByToken(@Param( "token" ) String userName) throws DaoException;

    //********************************************
    @Query(value = "SELECT u "
            + " FROM User u "
            + " WHERE u.id=:userId ")
    User findDetailsById( @Param("userId") long userId );

}