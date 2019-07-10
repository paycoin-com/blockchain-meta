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

import com.google.common.base.Strings;

import io.hs.bex.web.view.ModelView;


@Controller
public class CurrencyController
{
    @SuppressWarnings( "unused" )
    private static final Logger logger = LoggerFactory.getLogger( CurrencyController.class );

    @Autowired
    CurrencyService currencyService;

    @RequestMapping( value = { "/currency-list" }, method = RequestMethod.GET )
    public String currencyListView( ModelMap model )
    {
        model.addAttribute( "currencies", currencyService.getCurrencyList() );
        return ModelView.VIEW_CURRENCY_LIST_PAGE;
    }

    @RequestMapping( value = { "/currency-update" }, method = RequestMethod.POST )
    public String currencyUpdate( ModelMap model,
            @RequestParam( name = "supported", required = false ) String[] supported,
            @RequestParam( name = "code", required = false ) String code,
            @RequestParam( name = "details", required = false ) String details )
    {
        if( supported != null && supported.length > 0 )
            currencyService.setSupported( supported );
        else if( !Strings.isNullOrEmpty( code ) )
            currencyService.updateCurrency( code, details );

        return currencyListView( model );
    }

    @RequestMapping( value = { "/currency-sync-start" }, method = RequestMethod.GET )
    public String currencyStartSync( ModelMap model )
    {
        currencyService.startSyncJob();

        return currencyListView( model );
    }
    
    @RequestMapping( value = { "/currency-stats-create" }, method = RequestMethod.GET )
    public String currencyStatsCreate( ModelMap model )
    {
        currencyService.createStatsDataAsync();

        return currencyListView( model );
    }


    @RequestMapping( value = { "/currency-details" }, method = RequestMethod.GET )
    public String currencyDetailsView( ModelMap model, @RequestParam( name = "code" ) String code )
    {
        model.addAttribute( "currency", currencyService.getCurrencyDetails( code ) );
        return ModelView.VIEW_CURRENCY_DETAILS_PAGE;
    }

}
