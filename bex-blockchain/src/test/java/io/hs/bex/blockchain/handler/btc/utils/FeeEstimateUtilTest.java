package io.hs.bex.blockchain.handler.btc.utils;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;
import io.hs.bex.common.utils.MathUtils;
import io.hs.bex.common.utils.StringUtils;


//--------------------------------------------------------
class MempoolTxStats
{
    List<Map<Long, Double>> group = new ArrayList<Map<Long, Double>>();
}
// --------------------------------------------------------

@RunWith( MockitoJUnitRunner.class )
public class FeeEstimateUtilTest
{
    private final String ROOT_DIR = "/tmp";

    private final long EXTRAPOLATION_PERIOD = 20 * 60 * 1000; // 20 MINS

    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();

    // ----------------------------------------------------
    int[] feeRanges = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 17, 20, 25, 30, 40, 50, 60, 70, 80, 100, 120, 140, 170,
            200, 250, 300, 400, 500, 600, 700, 1700 };
    // ----------------------------------------------------

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    private long fetchStartTime = 0;
    private long lastMempoolSize = 0;

    private MempoolInfo memPoolInfo;
    private List<Double> extraPolateValues = new ArrayList<>();
    private List<MempoolTx> memPoolTxs;
    private ObjectMapper mapper = new ObjectMapper();
    private MempoolTxStats mempoolTxStats = new MempoolTxStats();

    String apiUrl = "https://btc.horizontalsystems.xyz";

    private BcoinHandler bcoinHandler = new BcoinHandler();

    public FeeEstimateUtilTest()
    {
        bcoinHandler.setMapper( mapper );
        bcoinHandler.init( "https://btc.horizontalsystems.xyz" );
    }

    @Before
    public void before() throws Exception
    {}

    public void creatElements() throws Exception
    {
        // ----------------------------------------------
        memPoolTxs = new ArrayList<MempoolTx>();
        // ----------------------------------------------

        MempoolTx mempoolTx1 = new MempoolTx( "HASH_1", 300, 1, System.currentTimeMillis() + ( 1 * 60 * 1000) );
        memPoolTxs.add( mempoolTx1 );

        MempoolTx mempoolTx2 = new MempoolTx( "HASH_2", 200, 2, System.currentTimeMillis() + ( 3 * 60 * 1000) );
        memPoolTxs.add( mempoolTx2 );

        MempoolTx mempoolTx3 = new MempoolTx( "HASH_3", 200, 4, System.currentTimeMillis() + ( 4 * 60 * 1000) );
        memPoolTxs.add( mempoolTx3 );

        MempoolTx mempoolTx4 = new MempoolTx( "HASH_4", 150, 8, System.currentTimeMillis() + ( 5 * 60 * 1000) );
        memPoolTxs.add( mempoolTx4 );

        MempoolTx mempoolTx5 = new MempoolTx( "HASH_5", 400, 16, System.currentTimeMillis() + ( 6 * 60 * 1000) );
        memPoolTxs.add( mempoolTx5 );

        // ----------------------------------------------
    }

    public void startScheduledAtFixed( int startAfter, int period )
    {
        timerService.scheduleAtFixedRate( () -> fetchMempoolStats(), 0, period, TimeUnit.SECONDS );
    }

    private void fetchElements( boolean init )
    {
        if( init == true )
        {
            mempoolTxStats.group.clear();
        }

        memPoolTxs = bcoinHandler.getMempoolTxs( 1 );

        setTxStats( memPoolTxs );
    }

    public void fetchMempoolStats()
    {
        memPoolInfo = bcoinHandler.getMempoolInfo();

        System.out.println( "*** Checking MemPool Size:" + memPoolInfo.getSize() );

        if( memPoolInfo.getSize() < lastMempoolSize )
        {
            fetchStartTime = System.currentTimeMillis();

            System.out.println( "*** Block event detected !!! *** " );
            System.out.println( "*** Starting fetching elements" );

            fetchElements( true );
        }
        else
        {
            if( fetchStartTime != 0 )
            {
                fetchElements( false );
                extraPolate();
            }
        }

        lastMempoolSize = memPoolInfo.getSize();
        memPoolInfo.setTime( System.currentTimeMillis() );
    }

    @Test
    public void estimateFee() throws InterruptedException
    {
        startScheduledAtFixed( 0, 30 );
        TimeUnit.SECONDS.sleep( 3000 );
    }

    public void extraPolate()
    {
        double xi = fetchStartTime + EXTRAPOLATION_PERIOD;
        double extarpValue = 0;
        int a = 0;

        extraPolateValues.clear();

        for( Map<Long, Double> dataMap: mempoolTxStats.group )
        {
            if( dataMap.size() > 1 )
            {
                if( ( fetchStartTime + EXTRAPOLATION_PERIOD) > System.currentTimeMillis() )
                {
                    double x[] = getXCoordinates( dataMap );
                    double y[] = getYCoordinates( dataMap );
                    extarpValue = MathUtils.linearInterp( x, y, xi );
                }
                else
                {
                    extarpValue = (double) dataMap.values().toArray()[dataMap.values().size() - 1];
                }

                extraPolateValues.add( extarpValue );

                System.out.println( "Group(" + feeRanges[a] + ") Extrapolate Size KBytes:" + extarpValue / 1000 );
                a++;
            }
        }

        estimeFee();

    }

    public void estimeFee()
    {
        int lastIndex = 0;
        double max = 0;

        for( int x = extraPolateValues.size() - 1; x >= 0; x-- )
        {
            if( max >= 1024 )
                break;

            max += ( extraPolateValues.get( x ) / 1000);
            lastIndex = x;
        }

        System.out.println( "*************************** " );
        System.out.println( "Estimating Fee S/Bytes:" + feeRanges[lastIndex] );
    }

    private void setTxStats( List<MempoolTx> memPoolTxs )
    {
        double maxValue = 0;
        double minValue = 0;
        double sum = 0;

        long time = System.currentTimeMillis();

        for( int x = 0; x < feeRanges.length; x++ )
        {
            minValue = (double) feeRanges[x];

            if( ( x + 1) == feeRanges.length )
                maxValue = feeRanges[x] * 1000;
            else
            {
                maxValue = (double) feeRanges[x + 1];
            }

            sum = getFilteredSum( minValue, maxValue );

            System.out.println(
                    "Sum of Group(" + feeRanges[x] + ") Size(Kbytes):=" + StringUtils.doubleToString( sum / 1000 ) );
            
            writeToFile( "sum_groups.txt", StringUtils.doubleToString( sum / 1000 ), true );

            if( mempoolTxStats.group.size() > x )
            {
                Map<Long, Double> dataMap = mempoolTxStats.group.get( x );
                dataMap.put( time, sum );
            }
            else
            {
                Map<Long, Double> dataMap = new LinkedHashMap<Long, Double>();
                dataMap.put( time, sum );
                mempoolTxStats.group.add( dataMap );
            }

        }

        System.out.println( "*************************" );
        writeToFile( "sum_groups.txt", "****************", true );

    }

    private void writeToFile( String fileName, String text, boolean append )
    {
        try
        {
            Files.write( Paths.get( ROOT_DIR + "/" + fileName ), text.getBytes(), StandardOpenOption.APPEND );
        }
        catch( IOException e )
        {
            // exception handling left as an exercise for the reader
        }
    }

    private double getFilteredSum( double minValue, double maxValue )
    {
        double sum = memPoolTxs.stream().filter( o -> ( minValue < o.getFeeRate() && o.getFeeRate() <= maxValue) )
                .mapToDouble( o -> o.getSize() ).sum();

        return sum;
    }

    private double[] getXCoordinates( Map<Long, Double> statGroups )
    {
        return statGroups.keySet().stream().mapToDouble( time -> time.longValue() ).toArray();
    }

    private double[] getYCoordinates( Map<Long, Double> statGroups )
    {
        return statGroups.values().stream().mapToDouble( size -> size.longValue() ).toArray();
    }

}
