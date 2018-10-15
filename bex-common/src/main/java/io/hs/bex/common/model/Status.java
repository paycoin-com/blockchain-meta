package io.hs.bex.common.model;

/**
 * 
 * @author nisakov
 *
 */
public enum Status
{
    UNASSIGNED(0),
    ACTIVE(1),
    INACTIVE(2),
    DISABLED(3);

    private short id;

    private Status( int id )
    {
        this.id = (short)id;
    }
    
    public short getId()
    {
        return id;
    }

}
