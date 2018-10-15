package io.hs.bex.common.service;

import java.io.IOException;

import io.hs.bex.common.utils.ImageUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.google.zxing.WriterException;

@Service("ImageService")
public class ImageService
{
    private ImageUtils imageUtils = new ImageUtils();
    
    public byte[] generateQRCode(String text, int width, int height)
    {
        try
        {
            return imageUtils.getQRCodeImage( text, width, height );
        }
        catch( IOException | WriterException e )
        {
            return null;
        }
    }
    
    @Async
    public ListenableFuture<byte[]> generateQRCodeAsync(String text, int width, int height) throws Exception 
    {
        return new AsyncResult<byte[]>(generateQRCode(text, width, height));
    }
    
}
