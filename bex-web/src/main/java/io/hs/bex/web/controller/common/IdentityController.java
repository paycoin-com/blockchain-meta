package io.hs.bex.web.controller.common;


import io.hs.bex.common.service.IdentityService;
import io.hs.bex.web.view.ModelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class IdentityController
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( IdentityController.class );
    // ---------------------------------

    @Autowired
    IdentityService identityService;

    // ***********************************************
    @RequestMapping( value = { "/login" }, method = RequestMethod.GET ) public String login(
            @RequestParam( value = "error", required = false ) String error,
            @RequestParam( value = "expired", required = false ) String expired, Model model)
    {
        if ( error != null )
        {
            //---------------------------------
            logger.info( "Invalid login " );
            //---------------------------------

            model.addAttribute( "errorMessage", "Invalid username or password" );
        }
        else if ( expired != null )
        {
            //---------------------------------
            logger.info( "Session expired for the user " );
            //---------------------------------

            model.addAttribute( "errorMessage", "Session expired." );
        }

        return ModelView.VIEW_SYSTEM_LOGIN_PAGE;
    }

    /*******************************************************
     *
     * */
    @RequestMapping( value={"/access_error_403","/access_denied"} )
    public String getAccessErrorView(Model model)
    {
        model.addAttribute( "errorData", "message.error.access.denied");
        return ModelView.VIEW_SYSTEM_ERROR_PAGE;
    }
}