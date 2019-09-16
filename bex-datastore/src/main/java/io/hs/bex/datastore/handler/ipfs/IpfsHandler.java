package io.hs.bex.datastore.handler.ipfs;


import javax.annotation.PostConstruct;

import io.hs.bex.datastore.handler.ipfs.model.FileInfoResponse;
import io.hs.bex.datastore.model.FileInfo;
import io.hs.bex.datastore.service.api.DataStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;


@Service( "IpfsHandler" )
public class IpfsHandler implements DataStoreHandler
{
    final private String API_SUB_URL = "/api/v0";

    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( IpfsHandler.class );
    // ---------------------------------

    @Value( "${node.ipfs.api.url}" )
    private String apiUrl;

    @Value( "${node.ipfs.gateway.url}" )
    private String gatewayUrl;

    @Value( "${node.ipfs.host}" )
    private String host;

    @Value( "${node.ipfs.root.dir}" )
    private String rootDir;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init()
    {
        restTemplate = new RestTemplate();
    }

    private HttpHeaders createHeader( MediaType mediType )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "user-agent", "Mozilla/5.0" );
        headers.setContentType( mediType );

        return headers;
    }
    
    @Override
    public String publishNS( String key, String path, String fileName )
    {
        FileInfo fileInfo = getFileInfo( path, fileName );
        
        if( fileInfo != null ) 
        {
            return publishNS( key, fileInfo.getHash() );
        }
        
        return "";
    }
    
    @Override
    public String publishNS( String key, String nsValue )
    {
        String url = apiUrl + API_SUB_URL + "/name/publish";

        try
        {
            url += "?arg=" + nsValue;
            
            if(!Strings.isNullOrEmpty( key ))
                url += "&key=" + key;

            HttpEntity<String> entity = new HttpEntity<String>( "parameters",
                    createHeader( MediaType.APPLICATION_JSON ) );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            if( response.getStatusCode() == HttpStatus.OK ) 
            {
                logger.info( "(!!!) **** Successfully published Hash:{}", nsValue );

                return "OK";
            }
            else
                return null;

        }
        catch( Exception e )
        {
            logger.error( "Error publishing IPFS hash:{}", nsValue, e );

            return null;
        }
    }
    

    @Override
    public FileInfo getFileInfo( String path, String fileName )
    {
        String url = apiUrl + API_SUB_URL + "/files/stat";

        try
        {
            if(!Strings.isNullOrEmpty( path ))
                path = rootDir + "/" + path;
            else
                path = rootDir + "/";
            
            url += "?arg=" + path + "&hash=true";

            HttpEntity<String> entity = new HttpEntity<String>( "parameters",
                    createHeader( MediaType.APPLICATION_JSON ) );
            ResponseEntity<FileInfoResponse> response = restTemplate.exchange( url, HttpMethod.GET, entity, 
                    FileInfoResponse.class );

            if( response.getStatusCode() == HttpStatus.OK ) 
            {
                return new FileInfo( response.getBody().getHash(),
                                     response.getBody().getCumulativeSize(),
                                     "directory".equals( response.getBody().getType()) ? true:false );
            }
            else
                return null;

        }
        catch( Exception e )
        {
            logger.error( "Error getting file info:{}", url, e );

            return null;
        }
    }

    @Override
    public String createDir( String path, boolean createParent )
    {
        String url = apiUrl + API_SUB_URL + "/files/mkdir";

        try
        {
            path = rootDir + "/" + path;
            url += "?arg=" + path + "&parents=" + createParent;

            HttpEntity<String> entity = new HttpEntity<String>( "parameters",
                    createHeader( MediaType.APPLICATION_JSON ) );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            if( response.getStatusCode() == HttpStatus.OK ) 
            {
                return "OK";
            }
            else
                return "";

        }
        catch( Exception e )
        {
            logger.error( "Error creating dir in ipfs:{}", url, e );

            return null;
        }
    }

    @Override
    public String saveFile( boolean createDir, String path, String fileName, String data )
    {
        String url = apiUrl + API_SUB_URL + "/files/write";

        try
        {
            // ---------------------------------
            if( createDir )
                createDir( path, true );
            // ---------------------------------

            path = rootDir + "/" + path + "/" + fileName;
            url += "?arg=" + path + "&truncate=true&create=true";

            MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
            bodyMap.add( "user-file", data.getBytes() );
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>( bodyMap,
                    createHeader( MediaType.MULTIPART_FORM_DATA ) );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );

            if( response.getStatusCode() == HttpStatus.OK )
                return "OK";
            else
                return "";

        }
        catch( Exception e )
        {
            logger.error( "Error saving file in ipfs:{}", url, e );

            return null;
        }
    }

    @Override
    public String getFileContent( String path, String fileName )
    {
        String url = apiUrl + API_SUB_URL + "/files/read";

        try
        {
            path = rootDir + "/" + path + "/" + fileName;
            url += "?arg=" + path;

            HttpEntity<String> entity = new HttpEntity<String>( "parameters",
                    createHeader( MediaType.APPLICATION_JSON ) );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            if( response.getStatusCode() == HttpStatus.OK )
                return response.getBody();
            else
                return "";

        }
        catch( HttpServerErrorException e )
        {
            //logger.error( "Error fetching file in ipfs:{}. File not found !!!", url );

            return null;
        }
        catch( Exception e )
        {
            logger.error( "Error fetching file in ipfs:{}", url, e );

            return null;
        }
    }

}
