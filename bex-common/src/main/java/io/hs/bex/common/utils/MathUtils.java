package io.hs.bex.common.utils;


import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;


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
}
