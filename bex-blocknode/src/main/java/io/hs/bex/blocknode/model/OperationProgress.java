package io.hs.bex.blocknode.model;

public class OperationProgress
{
    private float completePercentage = 0;
    private long blocksLeft = 0;
    
    public float getCompletePercentage()
    {
        return completePercentage;
    }
    
    public void setCompletePercentage( float completePercentage )
    {
        this.completePercentage = completePercentage;
    }

    public long getBlocksLeft()
    {
        return blocksLeft;
    }

    public void setBlocksLeft( long blocksLeft )
    {
        this.blocksLeft = blocksLeft;
    }
    
    
}
