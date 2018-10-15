package io.hs.bex.blockchain.model.store;

import java.util.ArrayList;
import java.util.List;

public class TxData extends MessageData
{
    private String blockHash;
    
    private List<TxOutputData> outputs = new ArrayList<>();
    private List<TxInputData> inputs = new ArrayList<>();

    public String getBlockHash()
    {
        return blockHash;
    }

    public void setBlockHash( String blockHash )
    {
        this.blockHash = blockHash;
    }

    public List<TxOutputData> getOutputs()
    {
        return outputs;
    }

    public void setOutputs( List<TxOutputData> outputs )
    {
        this.outputs = outputs;
    }

    public List<TxInputData> getInputs()
    {
        return inputs;
    }

    public void setInputs( List<TxInputData> inputs )
    {
        this.inputs = inputs;
    }
    
    
}
