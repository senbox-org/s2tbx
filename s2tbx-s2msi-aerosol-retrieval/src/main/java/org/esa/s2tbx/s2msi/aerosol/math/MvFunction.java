package org.esa.s2tbx.s2msi.aerosol.math;

/**
 * @author Andreas Heckel, Olaf Danne
 */
public interface MvFunction {

    /**
     *  multivariate function
     *
     * @param  x - point at which function should be calculated
     * @return     value of the function at x
     * @throws   UnsupportedOperationException -
     */
    double f(double[] x);

}
