package io.hs.bex.common.utils;


import java.util.Map;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;


public class MathUtils
{
    public static double linearInterp( double[] x, double[] y, double xi )
    {
        UnivariateInterpolator interpolator = new LinearInterpolator();
        UnivariateFunction function = interpolator.interpolate( x, y );
        PolynomialFunction[] polynomials = ( (PolynomialSplineFunction) function).getPolynomials();

        if( xi > x[x.length - 1] )
            return polynomials[polynomials.length - 1].value( xi - x[x.length - 2] );
        if( xi < x[0] )
            return polynomials[0].value( xi - x[0] );
        else
            return function.value( xi );
    }

    public static double linearRegression( double[] x, double[] y, double xi )
    {
        SimpleRegression regression = new SimpleRegression();

        for( int i = 0; i < x.length; i++ )
        {
            regression.addData( x[i], y[i] );
        }

        return regression.predict( xi );
    }

    public static double linearReg( Map<Long, Double> xyData, double xi )
    {
        SimpleRegression regression = new SimpleRegression();

        for( Long xValue: xyData.keySet() )
        {
            regression.addData( xValue, xyData.get( xValue ) );
        }

        return regression.predict( xi );
    }
    
    public static double linearRegAsInt( Map<Long, Integer> xyData, double xi )
    {
        SimpleRegression regression = new SimpleRegression();

        for( Long xValue: xyData.keySet() )
        {
            regression.addData( xValue, xyData.get( xValue ) );
        }

        return regression.predict( xi );
    }


}
