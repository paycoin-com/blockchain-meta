package io.hs.bex.web.controller.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.hs.bex.web.view.ModelView;


@Controller
public class SystemController
{
    private static final Logger logger = LoggerFactory.getLogger( SystemController.class );
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value={ "/", "/index", "/main"}, method = RequestMethod.GET)
    public String index( ModelMap model ) 
    {
        logger.info( "Index page loaded successfully !!! " );
        model.addAttribute( "tasks", "" );
        
        return ModelView.VIEW_SYSTEM_MAIN_PAGE;
    }
   
   
}
