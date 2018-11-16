package io.hs.bex.common.utils;


import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Strings;


public class StringUtils
{
    static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" )
            .withZone( ZoneId.systemDefault() );

    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd" )
            .withZone( ZoneId.systemDefault() );

    static DecimalFormat df = new DecimalFormat( "#0.0#########################" );

    public static String instantToString( Instant dateTime )
    {
        return DATE_TIME_FORMATTER.format( dateTime );
    }

    public static String localDateTimeToString( LocalDateTime localDate )
    {
        return localDate.format( DATE_TIME_FORMATTER );
    }

    public static LocalDateTime stringToDate( String dateStr )
    {
        if( dateStr.length() < 11 )
            return LocalDate.parse( dateStr, DATE_FORMATTER ).atStartOfDay();
        else
            return LocalDateTime.parse( dateStr, DATE_TIME_FORMATTER );
    }

    public static Instant stringToInstant( String dateStr, String format )
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( format )
                .withZone( ZoneId.systemDefault() );

        return Instant.from( formatter.parse( dateStr ) );
    }
    
    public static Instant stringToInstant( String dateStr )
    {
        if( dateStr.length() < 11 )
            return Instant.from( DATE_FORMATTER.parse( dateStr ) );
        else
            return Instant.from( DATE_TIME_FORMATTER.parse( dateStr ) );
    }


    public static String doubleToString( double value )
    {
        return df.format( value );
    }

    public static String[] splitToNChar( String text, int size )
    {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for( int i = 0; i < length; i += size )
        {
            parts.add( text.substring( i, Math.min( length, i + size ) ) );
        }

        return parts.toArray( new String[0] );
    }

    public static String createDirStructure( String text, int size )
    {
        String dir = File.separator;

        text = text.replace( File.separator, "" );

        int length = text.length();

        for( int i = 0; i < length; i += size )
        {
            dir += text.substring( i, Math.min( length, i + size ) ) + File.separator;
        }

        return dir;
    }

    public static String createDirStructure( String text, int size, int partsCount )
    {
        StringBuilder dir = new StringBuilder( File.separator );

        text = text.replace( File.separator, "" );

        int length = text.length();

        for( int i = 0; i < length; i += size )
        {
            dir.append( text.substring( i, Math.min( length, i + size ) ) + File.separator );

            if( ( --partsCount) == 0 )
            {
                dir.append( text.substring( Math.min( length, i + size ), length ) );
                if( !dir.toString().endsWith( "/" ) )
                    dir.append( File.separator );

                break;
            }
        }

        return dir.toString();
    }

    public static String toUnicodeValue( String unicode )
    {
        if( Strings.isNullOrEmpty( unicode ) )
            return "";

        unicode = unicode.replace( "U+", "\\u" );

        return StringEscapeUtils.unescapeJava( unicode );
    }

}
