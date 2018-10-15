package io.hs.bex.blocknode.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class NodeStatus
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( NodeStatus.class );
    // ---------------------------------
    
    private NodeState overallState = NodeState.UNDEFINED;

    
    private String message;
    
    private OperationType operationType = OperationType.IDLE;
    
    private OperationProgress operationProgress = new OperationProgress();
    
    public NodeStatus(){}
    
    public NodeStatus( String message )
    {
        this.message = message;
    }
    
    public String getMessage()
    {
        return message;
    }
    public void setMessage( String message )
    {
        this.message = message;
        
        //--------------------------------
        logger.info( "Status:{}", message );
        //--------------------------------
    }

    public OperationType getOperationType()
    {
        return operationType;
    }

    public void setOperationType( OperationType operationType )
    {
        this.operationType = operationType;
    }
    
    public OperationProgress getOperationProgress()
    {
        return operationProgress;
    }

    public void setOperationProgress( OperationProgress operationProgress )
    {
        this.operationProgress = operationProgress;
    }

    public NodeState getOverallState()
    {
        return overallState;
    }

    public void setOverallState( NodeState overallState )
    {
        this.overallState = overallState;
    }
  
}
