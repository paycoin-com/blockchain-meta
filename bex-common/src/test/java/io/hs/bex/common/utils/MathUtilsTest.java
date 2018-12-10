package io.hs.bex.common.utils;


import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Before;
import org.junit.Test;


public class MathUtilsTest
{

    @Before
    public void setUp() throws Exception
    {}
    

    @Test
    public void testInterpolation()
    {
        double x[] = { 10, 20, 30, 40, 50, 60 };
        double y[] = { 20, 30, 40, 60, 70, 80 };
        
        double xi =  70 ;
        double interpolatedY = 0;
        
        UnivariateInterpolator interpolator = new LinearInterpolator();
        UnivariateFunction function = interpolator.interpolate(x, y);
        PolynomialFunction[] polynomials = ((PolynomialSplineFunction) function).getPolynomials();
        
        if( xi > x[x.length - 1] )
            interpolatedY = polynomials[polynomials.length - 1].value( xi - x[x.length - 2] );
        else if( xi < x[0] )
            interpolatedY = polynomials[0].value( xi - x[0] );
        else
            interpolatedY =  function.value( xi );
            
        System.out.println("Extrapolation f(" + xi + ") = " + interpolatedY);
        
        assertTrue( interpolatedY == 90 );
    }

}
