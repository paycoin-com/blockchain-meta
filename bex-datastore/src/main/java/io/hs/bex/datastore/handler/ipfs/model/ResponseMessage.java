package io.hs.bex.datastore.handler.ipfs.model;

public class ResponseMessage
{
//    {
//        "Message": "file does not exist",
//        "Code": 0,
//        "Type": "error"
//    }
    
    private String message;
    private String code;
    private String type;
    
    public String getMessage()
    {
        return message;
    }
    public void setMessage( String message )
    {
        this.message = message;
    }
    public String getCode()
    {
        return code;
    }
    public void setCode( String code )
    {
        this.code = code;
    }
    public String getType()
    {
        return type;
    }
    public void setType( String type )
    {
        this.type = type;
    }
    
    
}
