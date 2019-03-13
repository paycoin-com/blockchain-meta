package io.hs.bex.blockchain.handler.eth.infura.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GasPriceResponse
{
    public GasPriceResponse() {}
    
    
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("result")
    private String result;

    public String getJsonrpc()
    {
        return jsonrpc;
    }

    public void setJsonrpc( String jsonrpc )
    {
        this.jsonrpc = jsonrpc;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult( String result )
    {
        this.result = result;
    }
    
    @JsonIgnore
    public int getGasPrice() 
    {
        return Integer.parseInt( result, 16);
    }
}
