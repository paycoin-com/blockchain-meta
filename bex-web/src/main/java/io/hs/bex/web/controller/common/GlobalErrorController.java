package io.hs.bex.web.controller.common;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class GlobalErrorController
{
    //---------------------------------
    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);
    //---------------------------------

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> defaultErrorHandler( HttpServletRequest request, Exception e ) throws Exception
    {
        
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;
     
        
        logger.error("*** Error occured and handled by Global handler ", e);
        
        return new ResponseEntity<Object>(
                "INTERNAL_SERVER_ERROR", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
    /*
     *  404 Error handler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handle( HttpServletRequest request, NoHandlerFoundException e ) 
    {
     
        
        logger.error("*** 404 Error occured in request URL:"+ request.getRequestURL() , e);
        
        return new ResponseEntity<Object>(
                "NOT_FOUND", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    
}
