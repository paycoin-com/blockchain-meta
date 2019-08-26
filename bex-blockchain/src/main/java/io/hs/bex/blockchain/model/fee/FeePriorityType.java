package io.hs.bex.blockchain.model.fee;


public enum FeePriorityType
{
    LOW( 1440, "low_priority"),
    MEDIUM( 120, "medium_priority"),
    HIGH( 30, "high_priority");

    private String displayName;
    private long defaultDuration;

    FeePriorityType ( long defaultDuration, String name )
    {
        this.displayName = name;
        this.defaultDuration = defaultDuration;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public long getDefaultDuration()
    {
        return defaultDuration;
    }
 }