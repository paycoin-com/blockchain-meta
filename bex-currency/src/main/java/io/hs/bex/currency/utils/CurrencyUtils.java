package io.hs.bex.currency.utils;

import java.time.LocalDateTime;

import io.hs.bex.currency.model.TimePeriod;

public class CurrencyUtils
{

    public static String buildDirStructure( TimePeriod timePeriod, String rootPath, LocalDateTime dateTime )
    {
        StringBuffer sb = new StringBuffer(rootPath);
        
        sb.append( String.format( "%02d", dateTime.getYear() ) + "/" )
          .append( String.format( "%02d", dateTime.getMonthValue() ) + "/")
          .append( String.format( "%02d", dateTime.getDayOfMonth() ) + "/");
        
          if(timePeriod == TimePeriod.HOUR || timePeriod == TimePeriod.MINUTE )
              sb.append( String.format( "%02d", dateTime.getHour() ) + "/");
          if(timePeriod == TimePeriod.MINUTE )
              sb.append( String.format( "%02d", dateTime.getMinute()));

        return sb.toString();
    }

}
