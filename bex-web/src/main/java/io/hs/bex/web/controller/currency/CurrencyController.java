package io.hs.bex.web.controller.currency;

import io.hs.bex.currency.service.api.CurrencyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.hs.bex.web.view.ModelView;


@Controller
public class CurrencyController
{
    @SuppressWarnings( "unused" )
    private static final Logger logger = LoggerFactory.getLogger( CurrencyController.class );
    
    @Autowired
    CurrencyService currencyService; 
    
    
    @RequestMapping(value={ "/currency-list"}, method = RequestMethod.GET)
    public String currencyListView( ModelMap model ) 
    {
        model.addAttribute( "currencies", currencyService.getCurrencyList());
        return ModelView.VIEW_CURRENCY_LIST_PAGE;
    }
    
    @RequestMapping(value={ "/currency-update"}, method = RequestMethod.GET)
    public String currencyUpdate( ModelMap model, @RequestParam( name="supported" ) String[] supported ) 
    {
        currencyService.setSupported( supported );
        
        return currencyListView( model );
    }
    
    @RequestMapping(value={ "//currency-sync-start"}, method = RequestMethod.GET)
    public String currencyStartSync( ModelMap model ) 
    {
        currencyService.startSyncJob();
        
        return currencyListView( model );
    }


}
