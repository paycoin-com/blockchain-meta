package io.hs.bex.common.utils;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class StringUtilsTest
{
    @Test
    public void testDoubleToString()
    {
        double a1 = 1.2;
        double a2 = 0.12345678910;
        double a3 = 123.120000;
        double a4 = 123456;
        
        String out1 = StringUtils.doubleToString( a1 );
        String out2 = StringUtils.doubleToString( a2 );
        String out3 = StringUtils.doubleToString( a3 );
        String out4 = StringUtils.doubleToString( a4 );
        
        System.out.println("Out1:" + out1 );
        System.out.println("Out2:" + out2 );
        System.out.println("Out3:" + out3 );
        System.out.println("Out4:" + out4 );
    }
    

    @Test
    public void testStringToNChar()
    {
        String out1 = "12312";
        int size = 3;
        
        String out[] = StringUtils.splitToNChar( out1, size );
        
        for(String pOut:out ) 
        {
            System.out.println("StringToNChar Original:" + out1 + " Out:" + pOut );
        }        
        
        assertTrue( out.length  == 2 );
    }
    
    @Test
    public void testCreateDirStructure()
    {
        String out1 = "dir/1dir2";
        int size = 4;
        
        String dir = StringUtils.createDirStructure( out1, size ); 
        
        System.out.println("StringToNChar Original:" + out1 + " Dir:" + dir );
        
        assertTrue( dir.length() == 11 );
    }
    
    @Test
    public void testCreateDirStructurePartsCount()
    {
        String out1 = "dir1dir2asdasdasdasdasd";
        int size = 4;
        int partsCount = 2;
        
        String dir = StringUtils.createDirStructure( out1, size , partsCount); 
        
        System.out.println("StringToNChar Parts Original:" + out1 + " Dir:" + dir );
        
        //assertTrue( dir.charAt( 3 ) == '1' );
    }

}
