package io.hs.bex.web.controller.common;


import java.util.concurrent.TimeUnit;
import io.hs.bex.common.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( value = "/api/v1/common/" )
public class CommonRestController
{
    @Autowired
    ImageService imageService;

    @GetMapping( value = "qrcode", produces = MediaType.IMAGE_PNG_VALUE )
    public ResponseEntity<byte[]> getQRCode( @RequestParam( value = "text", required = true ) String text )
    {
        try
        {
            return ResponseEntity.ok().cacheControl( CacheControl.maxAge( 30, TimeUnit.MINUTES ) )
                    .body( imageService.generateQRCodeAsync( text, 200, 200 ).get() );
        }
        catch( Exception ex )
        {
            //LOGGER.error( " Error starting block node by Id:{}", nodeId, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

}
