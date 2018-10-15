package io.hs.bex.common.model;

import java.util.List;

public class CollectionTracker<E>
{   
    public static int MAX_ACTIVE_ITEMS = 12;
    
    private String prevId;
    
    private String nextId;
    
    private String searchParam;
    
    private int index = 0;
    
    List<E> items;

    public String getPrevId()
    {
        return prevId;
    }

    public void setPrevId( String prevId )
    {
        this.prevId = prevId;
    }

    public String getNextId()
    {
        return nextId;
    }

    public void setNextId( String nextId )
    {
        this.nextId = nextId;
    }

    public String getSearchParam()
    {
        return searchParam;
    }

    public void setSearchParam( String searchParam )
    {
        this.searchParam = searchParam;
    }

    public int getTotalSize()
    {
        return items.size();
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public List<E> getItems()
    {
        return items;
    }

    public void setItems( List<E> items )
    {
        this.items = items;
    }


}
